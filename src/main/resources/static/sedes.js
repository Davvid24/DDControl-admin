

let todasLasSedes  = [];
let selectedSedeId = null;
let sedeEditandoId = null;

async function cargarSedes() {
  try {
    todasLasSedes = await apiFetch(`${API}/sedes`);
    renderTable();
  } catch (e) {
    showToast(e.message || 'Error al cargar sedes', 'error');
  }
}

function renderTable() {
  document.querySelector('#sedesTable tbody').innerHTML = todasLasSedes.length
    ? todasLasSedes.map(s => `
      <tr class="${selectedSedeId === s.id ? 'sel' : ''}"
          onclick="selectSede(${s.id})" style="cursor:pointer">
        <td style="font-weight:600">${s.nombre}</td>
        <td>${s.direccion || '—'}</td>
        <td>—</td>
        <td><code style="font-family:var(--mono);font-size:12px">${s.radioMetros} m</code></td>
        <td>${s.activa
          ? '<span class="badge badge-green">Activa</span>'
          : '<span class="badge badge-gray">Inactiva</span>'}</td>
        <td><div class="actions">
          <button class="act-btn act-edit"
            onclick="event.stopPropagation();editarSede(${s.id})">Editar</button>
          <button class="act-btn act-delete"
            onclick="event.stopPropagation();confirmDelete('¿Eliminar esta sede?',()=>eliminarSede(${s.id}))">Eliminar</button>
        </div></td>
      </tr>`).join('')
    : `<tr><td colspan="6" style="text-align:center;padding:40px;color:var(--text-label)">
         No hay sedes registradas
       </td></tr>`;
}

// ── SELECCIONAR SEDE (panel detalle) ─────────────────────────
function selectSede(id) {
  selectedSedeId = id;
  const s = todasLasSedes.find(x => x.id === id);
  if (!s) return;
  document.getElementById('mapLabel').textContent   = s.nombre;
  document.getElementById('det-nombre').textContent = s.nombre;
  document.getElementById('det-dir').textContent    = s.direccion  || '—';
  document.getElementById('det-coords').textContent =
    `${s.latitud ?? '—'}, ${s.longitud ?? '—'}`;
  document.getElementById('det-radio').textContent  = `${s.radioMetros} m`;
  document.getElementById('det-emp').textContent    = '—';
  renderTable();
}

// ── EDITAR ───────────────────────────────────────────────────
function editarSede(id) {
  const s = todasLasSedes.find(x => x.id === id);
  if (!s) return;
  sedeEditandoId = id;
  document.getElementById('modal-sede-title').textContent = 'Editar sede';
  document.getElementById('sedeNombre').value    = s.nombre;
  document.getElementById('sedeDireccion').value = s.direccion  || '';
  document.getElementById('sedeLat').value       = s.latitud    ?? '';
  document.getElementById('sedeLon').value       = s.longitud   ?? '';
  document.getElementById('sedeRadio').value     = s.radioMetros;
  document.getElementById('sedeEstado').value    = s.activa ? 'activa' : 'inactiva';
  openModal('modal-sede');
}

// ── NUEVA SEDE (reset form) ───────────────────────────────────
function abrirModalNueva() {
  sedeEditandoId = null;
  document.getElementById('modal-sede-title').textContent = 'Nueva sede';
  ['sedeNombre','sedeDireccion','sedeLat','sedeLon'].forEach(id => {
    document.getElementById(id).value = '';
  });
  document.getElementById('sedeRadio').value  = '100';
  document.getElementById('sedeEstado').value = 'activa';
  openModal('modal-sede');
}

// ── GUARDAR ──────────────────────────────────────────────────
async function guardarSede() {
  const nombre    = document.getElementById('sedeNombre').value.trim();
  const direccion = document.getElementById('sedeDireccion').value.trim();
  const latitud   = parseFloat(document.getElementById('sedeLat').value);
  const longitud  = parseFloat(document.getElementById('sedeLon').value);
  const radio     = parseInt(document.getElementById('sedeRadio').value);
  const activa    = document.getElementById('sedeEstado').value === 'activa';

  if (!nombre)             { showToast('El nombre es obligatorio', 'error');          return; }
  if (isNaN(latitud) || isNaN(longitud)) {
    showToast('Introduce coordenadas válidas', 'error'); return;
  }

  const body = {
    idEmpresa: parseInt(sessionStorage.getItem('idEmpresa')),
    nombre, direccion: direccion || null,
    latitud, longitud, radioMetros: radio, activa
  };

  try {
    if (sedeEditandoId) {
      await apiFetch(`${API}/sedes/${sedeEditandoId}`, {
        method: 'PUT', body: JSON.stringify(body)
      });
      showToast('Sede actualizada', 'success');
    } else {
      await apiFetch(`${API}/sedes`, {
        method: 'POST', body: JSON.stringify(body)
      });
      showToast('Sede creada correctamente', 'success');
    }
    sedeEditandoId = null;
    closeModal('modal-sede');
    cargarSedes();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

// ── ELIMINAR ─────────────────────────────────────────────────
async function eliminarSede(id) {
  try {
    await apiFetch(`${API}/sedes/${id}`, { method: 'DELETE' });
    if (selectedSedeId === id) selectedSedeId = null;
    showToast('Sede eliminada', 'success');
    cargarSedes();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

// ── INIT ─────────────────────────────────────────────────────
cargarSedes();
