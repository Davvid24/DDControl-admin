
const PAGE_SIZE = 10;
let currentPage   = 1;
let todosLosFichajes = [];
let filteredData  = [];

async function cargarFichajes() {
  try {
    todosLosFichajes = await apiFetch(`${API}/fichajes`);
    filteredData = [...todosLosFichajes];
    renderTable();
  } catch (e) {
    showToast(e.message || 'Error al cargar fichajes', 'error');
  }
}

function applyFilters() {
  const emp  = document.getElementById('filtroEmpleado').value.toLowerCase();
  const sede = document.getElementById('filtroSede').value.toLowerCase();
  const tipo = document.getElementById('filtroTipo').value.toLowerCase();

  filteredData = todosLosFichajes.filter(f => {
    if (emp  && !(f.nombreUsuario || '').toLowerCase().includes(emp))  return false;
    if (sede && !(f.nombreSede    || '').toLowerCase().includes(sede)) return false;
    if (tipo && f.tipo !== tipo) return false;
    return true;
  });
  currentPage = 1;
  renderTable();
}

function renderTable() {
  const start = (currentPage - 1) * PAGE_SIZE;
  const page  = filteredData.slice(start, start + PAGE_SIZE);

  document.querySelector('#fichajesTable tbody').innerHTML = page.length
    ? page.map(f => {
        const nombre    = f.nombreUsuario || '—';
        const color     = avatarColor(nombre);
        const ini       = initials(nombre);
        const tipoBadge = f.tipo === 'entrada'
          ? '<span class="badge badge-green">Entrada</span>'
          : '<span class="badge badge-red">Salida</span>';
        const gpsBadge  = f.dentroDeRadio
          ? '<span class="badge badge-green">✓ Dentro</span>'
          : '<span class="badge badge-red">✗ Fuera</span>';
        const metBadge  = f.metodo === 'movil'
          ? '<span class="badge badge-blue">Móvil</span>'
          : '<span class="badge badge-gray">Manual</span>';
        const hora      = f.timestampFicha ? formatTime(f.timestampFicha) : '—';

        return `<tr>
          <td><div class="emp-cell">
            <div class="emp-avatar" style="background:${color}">${ini}</div>
            ${nombre}
          </div></td>
          <td>${tipoBadge}</td>
          <td><code style="font-family:var(--mono);font-size:12px">${hora}</code></td>
          <td>${f.nombreSede || '—'}</td>
          <td>${gpsBadge}</td>
          <td>${metBadge}</td>
          <td><div class="actions">
            <button class="act-btn act-delete"
              onclick="confirmDelete('¿Eliminar este fichaje?', () => eliminarFichaje(${f.id}))">
              Eliminar
            </button>
          </div></td>
        </tr>`;
      }).join('')
    : `<tr><td colspan="7" style="text-align:center;padding:40px;color:var(--text-label)">
         No hay fichajes para los filtros seleccionados
       </td></tr>`;

  document.getElementById('pagInfo').textContent =
    `Mostrando ${Math.min(page.length, PAGE_SIZE)} de ${filteredData.length} fichajes`;
  document.getElementById('pagNum').textContent = currentPage;
}

function changePage(dir) {
  const max = Math.ceil(filteredData.length / PAGE_SIZE) || 1;
  currentPage = Math.max(1, Math.min(max, currentPage + dir));
  renderTable();
}

async function guardarFichaje() {
  const idUsuario = parseInt(document.getElementById('fichajeUsuario').value);
  const idSede    = parseInt(document.getElementById('fichajeSede').value);
  const tipo      = document.getElementById('fichajeTipo').value;
  const obs       = document.getElementById('fichajeObs').value.trim();

  if (!idUsuario || !idSede) { showToast('Selecciona empleado y sede', 'error'); return; }

  const body = {
    idUsuario,
    idSede,
    tipo,
    latitudReal:  0,
    longitudReal: 0,
    metodo:       'manual',
    observaciones: obs || null
  };

  try {
    await apiFetch(`${API}/fichajes`, { method: 'POST', body: JSON.stringify(body) });
    closeModal('modal-fichaje');
    showToast('Fichaje registrado correctamente', 'success');
    cargarFichajes();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

async function eliminarFichaje(id) {
  try {
    await apiFetch(`${API}/fichajes/${id}`, { method: 'DELETE' });
    showToast('Fichaje eliminado', 'success');
    cargarFichajes();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

function exportCSV() {
  const rows = [['Empleado','Tipo','Hora','Sede','GPS','Método']];
  filteredData.forEach(f => rows.push([
    f.nombreUsuario || '',
    f.tipo,
    f.timestampFicha ? formatTime(f.timestampFicha) : '',
    f.nombreSede || '',
    f.dentroDeRadio ? 'Dentro' : 'Fuera',
    f.metodo
  ]));
  const csv = rows.map(r => r.join(',')).join('\n');
  const a = document.createElement('a');
  a.href = 'data:text/csv;charset=utf-8,' + encodeURIComponent(csv);
  a.download = 'fichajes.csv';
  a.click();
}

async function cargarSelectores() {
  try {
    const [usuarios, sedes] = await Promise.all([
      apiFetch(`${API}/usuarios`),
      apiFetch(`${API}/sedes`)
    ]);
    const selUsr = document.getElementById('fichajeUsuario');
    const selSed = document.getElementById('fichajeSede');
    if (selUsr) selUsr.innerHTML = usuarios.map(u =>
      `<option value="${u.id}">${u.nombre} ${u.apellidos}</option>`).join('');
    if (selSed) selSed.innerHTML = sedes.map(s =>
      `<option value="${s.id}">${s.nombre}</option>`).join('');
  } catch {  }
}

['filtroEmpleado','filtroSede','filtroTipo','filtroDesde','filtroHasta']
  .forEach(id => document.getElementById(id)?.addEventListener('change', applyFilters));

cargarFichajes();
cargarSelectores();
