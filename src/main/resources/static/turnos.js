let todosLosTurnos   = [];
let todosLosEmpleados = [];
let turnoAsignandoId  = null;
let turnoEditandoId   = null;

async function cargarTurnos() {
    try {
        todosLosTurnos = await apiFetch(`${API}/turnos`);
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar turnos', 'error');
    }
}

async function cargarEmpleados() {
    try {
        const idEmpresa = parseInt(sessionStorage.getItem('empresaId'));
        todosLosEmpleados = await apiFetch(`${API}/usuarios/empresa/${idEmpresa}`);
    } catch (e) {
        showToast('Error al cargar empleados', 'error');
    }
}

function renderTable() {
    document.querySelector('#turnosTable tbody').innerHTML = todosLosTurnos.length
        ? todosLosTurnos.map(t => {
            const numEmp = t.empleados?.length ?? 0;
            return `<tr>
                <td style="font-weight:600">${t.nombre}</td>
                <td><code style="font-family:var(--mono)">${t.horaEntrada}</code></td>
                <td><code style="font-family:var(--mono)">${t.horaSalida}</code></td>
                <td style="color:var(--text-muted);font-size:12px">${t.descripcion || '—'}</td>
                <td>
                    <button class="act-btn" onclick="abrirAsignar(${t.id})">
                        ${numEmp} empleado${numEmp !== 1 ? 's' : ''}
                    </button>
                </td>
                <td><div class="actions">
                    <button class="act-btn act-edit"
                        onclick="editarTurno(${t.id})">Editar</button>
                    <button class="act-btn act-delete"
                        onclick="confirmDelete('¿Eliminar turno?', () => eliminarTurno(${t.id}))">Eliminar</button>
                </div></td>
            </tr>`;
        }).join('')
        : `<tr><td colspan="6" style="text-align:center;padding:40px;color:var(--text-label)">
             No hay turnos definidos
           </td></tr>`;
}

async function abrirAsignar(idTurno) {
    turnoAsignandoId = idTurno;
    const turno = todosLosTurnos.find(t => t.id === idTurno);
    document.getElementById('asignar-turno-nombre').textContent = turno?.nombre ?? '';

    // Empleados ya asignados a este turno
    const asignadosIds = new Set((turno?.empleados ?? []).map(e => e.id));

    document.getElementById('asignar-lista').innerHTML = todosLosEmpleados.length
        ? todosLosEmpleados.map(e => {
            const checked = asignadosIds.has(e.id) ? 'checked' : '';
            const nombre  = `${e.nombre} ${e.apellidos}`;
            const color   = avatarColor(nombre);
            const ini     = initials(nombre);
            return `<label style="display:flex;align-items:center;gap:12px;padding:8px 10px;
                                  border-radius:8px;cursor:pointer;transition:background .15s"
                          onmouseover="this.style.background='var(--surface)'"
                          onmouseout="this.style.background=''"}>
                <input type="checkbox" value="${e.id}" ${checked}
                       style="width:16px;height:16px;accent-color:var(--blue);cursor:pointer">
                <div style="width:32px;height:32px;border-radius:50%;background:${color};
                            display:flex;align-items:center;justify-content:center;
                            color:white;font-size:12px;font-weight:700;flex-shrink:0">${ini}</div>
                <div>
                    <div style="font-weight:600;font-size:13px">${nombre}</div>
                    <div style="font-size:12px;color:var(--text-muted)">${e.email}</div>
                </div>
            </label>`;
        }).join('')
        : `<p style="color:var(--text-muted);text-align:center;padding:20px">
             No hay empleados disponibles
           </p>`;

    openModal('modal-asignar');
}

async function confirmarAsignacion() {
    const checkboxes = document.querySelectorAll('#asignar-lista input[type=checkbox]');
    const ids = [...checkboxes].filter(c => c.checked).map(c => parseInt(c.value));

    try {
        await apiFetch(`${API}/turnos/${turnoAsignandoId}/asignar-empleados`, {
            method: 'PATCH',
            body: JSON.stringify(ids)
        });
        showToast('Asignación guardada', 'success');
        closeModal('modal-asignar');
        cargarTurnos();
    } catch (e) {
        showToast(e.message, 'error');
    }
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

    if (!nombre)             { showToast('El nombre es obligatorio', 'error');   return; }
    if (!entrada || !salida) { showToast('Las horas son obligatorias', 'error'); return; }

    const body = {
        idEmpresa:   parseInt(sessionStorage.getItem('empresaId')),
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
    document.getElementById('turnoId').value          = '';
    document.getElementById('turnoNombre').value      = '';
    document.getElementById('turnoEntrada').value     = '08:00';
    document.getElementById('turnoSalida').value      = '16:00';
    document.getElementById('turnoDesc').value        = '';
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

cargarEmpleados();
cargarTurnos();