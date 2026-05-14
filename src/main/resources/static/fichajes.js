const PAGE_SIZE = 10;
let currentPage      = 1;
let todosLosFichajes = [];
let filteredData     = [];

async function cargarFichajes() {
    try {
        todosLosFichajes = await apiFetch(`${API}/fichajes`);
        todosLosFichajes.sort((a, b) => new Date(b.timestampFicha) - new Date(a.timestampFicha));
        filteredData = [...todosLosFichajes];
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar fichajes', 'error');
    }
}

function applyFilters() {
    const emp   = (document.getElementById('filtroEmpleado')?.value || '').toLowerCase().trim();
    const sede  = (document.getElementById('filtroSede')?.value    || '').toLowerCase().trim();
    const tipo  = (document.getElementById('filtroTipo')?.value    || '').toLowerCase().trim();
    const desde = document.getElementById('filtroDesde')?.value || '';
    const hasta = document.getElementById('filtroHasta')?.value  || '';

    filteredData = todosLosFichajes.filter(f => {
        if (emp  && !(f.nombreUsuario || '').toLowerCase().includes(emp))  return false;
        if (sede && !(f.nombreSede    || '').toLowerCase().includes(sede)) return false;
        if (tipo && !(f.tipo          || '').toLowerCase().includes(tipo)) return false;
        if (desde || hasta) {
            const fecha = f.timestampFicha ? f.timestampFicha.substring(0, 10) : '';
            if (desde && fecha < desde) return false;
            if (hasta && fecha > hasta) return false;
        }
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
            const tipoBadge = f.tipo === 'entrada'      ? `<span class="badge badge-green">${t('comun.entrada')}</span>`
                : f.tipo === 'salida'       ? `<span class="badge badge-red">${t('comun.salida')}</span>`
                    : f.tipo === 'pausa_inicio' ? `<span class="badge badge-yellow">${t('fichajes.pausa_ini')}</span>`
                        : f.tipo === 'pausa_fin'    ? `<span class="badge badge-blue">${t('fichajes.pausa_fin')}</span>`
                            : `<span class="badge badge-gray">${f.tipo}</span>`;

            const gpsBadge = f.dentroDeRadio
                ? `<span class="badge badge-green">${t('comun.dentro')}</span>`
                : `<span class="badge badge-red">${t('comun.fuera')}</span>`;

            const metBadge = f.metodo === 'movil'
                ? `<span class="badge badge-blue">${t('fichajes.movil')}</span>`
                : `<span class="badge badge-gray">${t('fichajes.manual')}</span>`;
            const hora = f.timestampFicha ? formatDateTime(f.timestampFicha) : '—';
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
            ${t('accion.eliminar')}
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
function formatDateTime(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    const fecha = d.toLocaleDateString('es-ES', { day: '2-digit', month: '2-digit', year: 'numeric' });
    const hora  = d.toLocaleTimeString('es-ES', { hour: '2-digit', minute: '2-digit' });
    return `<span style="display:block;font-weight:600">${hora}</span><span style="font-size:11px;color:var(--text-muted)">${fecha}</span>`;
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

        const listaSedes = document.getElementById('listaSedesFichajes');
        if (listaSedes) sedes.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.nombre;
            listaSedes.appendChild(opt);
        });
    } catch { }
}

['filtroEmpleado', 'filtroSede', 'filtroTipo'].forEach(id =>
    document.getElementById(id)?.addEventListener('input', applyFilters)
);
['filtroDesde', 'filtroHasta'].forEach(id =>
    document.getElementById(id)?.addEventListener('change', applyFilters)
);

cargarFichajes();
cargarSelectores();