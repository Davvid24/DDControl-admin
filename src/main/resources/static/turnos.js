
let todosLosTurnos = [];
let turnoEditandoId = null;

async function cargarTurnos() {
  try {
    todosLosTurnos = await apiFetch(`${API}/turnos`);
    renderTable();
  } catch (e) {
    showToast(e.message || 'Error al cargar turnos', 'error');
  }
}

function renderTable() {
  document.querySelector('#turnosTable tbody').innerHTML = todosLosTurnos.length
    ? todosLosTurnos.map(t => `
      <tr>
        <td style="font-weight:600">${t.nombre}</td>
        <td><code style="font-family:var(--mono)">${t.horaEntrada}</code></td>
        <td><code style="font-family:var(--mono)">${t.horaSalida}</code></td>
        <td style="color:var(--text-muted);font-size:12px">${t.descripcion || '—'}</td>
        <td>—</td>
        <td><div class="actions">
          <button class="act-btn act-edit"
            onclick="editarTurno(${t.id})">Editar</button>
          <button class="act-btn act-delete"
            onclick="confirmDelete('¿Eliminar turno?', () => eliminarTurno(${t.id}))">Eliminar</button>
        </div></td>
      </tr>`).join('')
    : `<tr><td colspan="6" style="text-align:center;padding:40px;color:var(--text-label)">
         No hay turnos definidos
       </td></tr>`;
}

function editarTurno(id) {
  const t = todosLosTurnos.find(x => x.id === id);
  if (!t) return;
  turnoEditandoId = id;
  document.getElementById('turnoId').value       = t.id;
  document.getElementById('turnoNombre').value   = t.nombre;
  document.getElementById('turnoEntrada').value  = t.horaEntrada;
  document.getElementById('turnoSalida').value   = t.horaSalida;
  document.getElementById('turnoDesc').value     = t.descripcion || '';
  document.getElementById('form-title').textContent = 'Editar turno';
  resetDias();
}

async function guardarTurno() {
  const nombre  = document.getElementById('turnoNombre').value.trim();
  const entrada = document.getElementById('turnoEntrada').value;
  const salida  = document.getElementById('turnoSalida').value;
  const desc    = document.getElementById('turnoDesc').value.trim();
  const id      = turnoEditandoId;

  if (!nombre)           { showToast('El nombre es obligatorio', 'error');   return; }
  if (!entrada || !salida) { showToast('Las horas son obligatorias', 'error'); return; }

  const body = {
    idEmpresa:   parseInt(sessionStorage.getItem('idEmpresa')),
    nombre,
    horaEntrada: entrada,
    horaSalida:  salida,
    descripcion: desc || null
  };

  try {
    if (id) {
      await apiFetch(`${API}/turnos/${id}`, { method: 'PUT', body: JSON.stringify(body) });
      showToast('Turno actualizado', 'success');
    } else {
      await apiFetch(`${API}/turnos`, { method: 'POST', body: JSON.stringify(body) });
      showToast('Turno creado correctamente', 'success');
    }
    resetForm();
    cargarTurnos();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

async function eliminarTurno(id) {
  try {
    await apiFetch(`${API}/turnos/${id}`, { method: 'DELETE' });
    showToast('Turno eliminado', 'success');
    cargarTurnos();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

function resetForm() {
  turnoEditandoId = null;
  document.getElementById('turnoId').value       = '';
  document.getElementById('turnoNombre').value   = '';
  document.getElementById('turnoEntrada').value  = '08:00';
  document.getElementById('turnoSalida').value   = '16:00';
  document.getElementById('turnoDesc').value     = '';
  document.getElementById('form-title').textContent = 'Nuevo turno';
  resetDias();
}

function resetDias() {
  document.querySelectorAll('.day-btn').forEach(btn => {
    const defaultOn = ['L','M','X','J','V'].includes(btn.dataset.day);
    btn.classList.toggle('day-on',  defaultOn);
    btn.classList.toggle('day-off', !defaultOn);
  });
}

cargarTurnos();
