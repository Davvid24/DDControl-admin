let todosLosEmpleados = [];
let empleadoEditandoId = null;
let nuevaPasswordPendiente = null;

async function cargarEmpleados() {
    try {
        todosLosEmpleados = await apiFetch(`${API}/usuarios`);
        for (const e of todosLosEmpleados) {
            const sedes = await apiFetch(`${API}/empleado-sede/usuario/${e.id}`);
            e.nombreSede   = sedes?.length > 0 ? sedes[0].nombreSede : '—';
            e.idSedeActual = sedes?.length > 0 ? sedes[0].idSede     : null;
        }
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar empleados', 'error');
    }
}

async function cargarSedesYTurnos() {
    try {
        const idEmpresa = parseInt(sessionStorage.getItem('empresaId'));

        const sedes = await apiFetch(`${API}/sedes?idEmpresa=${idEmpresa}`);
        const listaSedes = document.getElementById('listaSedes');
        ['nuevoSede', 'editSede'].forEach(id => {
            const sel = document.getElementById(id);
            sedes.forEach(s => sel.insertAdjacentHTML('beforeend', `<option value="${s.id}">${s.nombre}</option>`));
        });
        sedes.forEach(s => {
            const opt = document.createElement('option');
            opt.value = s.nombre;
            listaSedes.appendChild(opt);
        });

        const turnos = await apiFetch(`${API}/turnos?idEmpresa=${idEmpresa}`);
        ['nuevoTurno', 'editTurno'].forEach(id => {
            const sel = document.getElementById(id);
            turnos.forEach(t => sel.insertAdjacentHTML('beforeend', `<option value="${t.id}">${t.nombre}</option>`));
        });
    } catch (e) {
        showToast('Error al cargar sedes/turnos', 'error');
    }
}

function estadoBadge(activo) {
    return activo
        ? '<span class="badge badge-green"><span class="dot dot-green"></span>Activo</span>'
        : '<span class="badge badge-gray">Inactivo</span>';
}

function renderTable() {
    const busqueda   = (document.getElementById('busqueda')?.value          || '').toLowerCase().trim();
    const filtroRol  = (document.getElementById('filtroRolInput')?.value    || '').toLowerCase().trim();
    const filtroSede = (document.getElementById('filtroSedeInput')?.value   || '').toLowerCase().trim();
    const filtroEst  = (document.getElementById('filtroEstadoInput')?.value || '').toLowerCase().trim();

    const data = todosLosEmpleados.filter(e => {
        if (filtroRol  && !e.rol.toLowerCase().includes(filtroRol))                  return false;
        if (filtroSede && !(e.nombreSede || '').toLowerCase().includes(filtroSede))  return false;
        if (filtroEst === 'activo'   && !e.activo)  return false;
        if (filtroEst === 'inactivo' &&  e.activo)  return false;
        if (busqueda) {
            const nombre = `${e.nombre} ${e.apellidos}`.toLowerCase();
            const email  = (e.email        || '').toLowerCase();
            const puesto = (e.tipoEmpleado || '').toLowerCase();
            const sede   = (e.nombreSede   || '').toLowerCase();
            if (!nombre.includes(busqueda) &&
                !email.includes(busqueda)  &&
                !puesto.includes(busqueda) &&
                !sede.includes(busqueda))   return false;
        }
        return true;
    });

    document.querySelector('#empleadosTable tbody').innerHTML = data.length
        ? data.map(e => {
            const nombre = `${e.nombre} ${e.apellidos}`;
            const color  = avatarColor(nombre);
            const ini    = initials(nombre);
            return `<tr>
              <td><div class="emp-cell">
                <div class="emp-avatar" style="background:${color}">${ini}</div>
                <div class="emp-info">
                  <div class="emp-name">${nombre}</div>
                  <div class="emp-email">${e.email}</div>
                </div>
              </div></td>
              <td>${e.tipoEmpleado || '—'}</td>
              <td>${e.nombreSede   || '—'}</td>
              <td>${e.rol}</td>
              <td>${estadoBadge(e.activo)}</td>
              <td style="color:var(--text-muted);font-size:12px">${formatDate(e.fechaAlta)}</td>
              <td><div class="actions">
                <button class="act-btn act-edit"   onclick="editarEmpleado(${e.id})">Editar</button>
                <button class="act-btn act-delete" onclick="toggleActivo(${e.id}, ${e.activo})">
                  ${e.activo ? 'Desactivar' : 'Activar'}
                </button>
              </div></td>
            </tr>`;
        }).join('')
        : `<tr><td colspan="7" style="text-align:center;padding:40px;color:var(--text-label)">
             No se encontraron empleados
           </td></tr>`;
}

function limpiarFiltros() {
    document.getElementById('busqueda').value          = '';
    document.getElementById('filtroRolInput').value    = '';
    document.getElementById('filtroSedeInput').value   = '';
    document.getElementById('filtroEstadoInput').value = '';
    renderTable();
}

function editarEmpleado(id) {
    const e = todosLosEmpleados.find(x => x.id === id);
    if (!e) return;
    empleadoEditandoId     = id;
    nuevaPasswordPendiente = null;

    document.getElementById('editNombre').value    = e.nombre;
    document.getElementById('editApellidos').value = e.apellidos;
    document.getElementById('editEmail').value     = e.email;
    document.getElementById('editTel').value       = e.telefono || '';
    document.getElementById('editRol').value       = e.rol;
    document.getElementById('editTipo').value      = e.tipoEmpleado || 'Comercial';
    if (e.idSedeActual) document.getElementById('editSede').value  = e.idSedeActual;
    if (e.idTurno)      document.getElementById('editTurno').value = e.idTurno;

    ocultarError('editErrorBox');
    openModal('modal-empleado-editar');
}

async function guardarEdicionEmpleado() {
    const tel     = document.getElementById('editTel').value.trim();
    const rol     = document.getElementById('editRol').value;
    const tipo    = document.getElementById('editTipo').value;
    const idSede  = document.getElementById('editSede').value;
    const idTurno = document.getElementById('editTurno').value;

    if (tel && !/^\+?[\d\s\-]{7,15}$/.test(tel)) {
        mostrarError('editErrorBox', 'El teléfono no tiene un formato válido.'); return;
    }

    const e = todosLosEmpleados.find(x => x.id === empleadoEditandoId);
    const body = {
        idEmpresa:    parseInt(sessionStorage.getItem('empresaId')),
        nombre:       e.nombre,
        apellidos:    e.apellidos,
        email:        e.email,
        rol,
        tipoEmpleado: tipo,
        telefono:     tel || null,
        idTurno:      idTurno ? parseInt(idTurno) : null,
    };

    if (nuevaPasswordPendiente) body.password = nuevaPasswordPendiente;

    try {
        await apiFetch(`${API}/usuarios/${empleadoEditandoId}`, {
            method: 'PUT', body: JSON.stringify(body)
        });
        await actualizarSede(empleadoEditandoId, idSede);
        showToast('Empleado actualizado correctamente', 'success');
        empleadoEditandoId     = null;
        nuevaPasswordPendiente = null;
        closeModal('modal-empleado-editar');
        cargarEmpleados();
    } catch (e) {
        mostrarError('editErrorBox', e.message || 'Error al guardar');
    }
}

function abrirModalPassword() {
    document.getElementById('newPass').value                    = '';
    document.getElementById('confirmPass').value                = '';
    document.getElementById('passStrengthBar').style.width      = '0%';
    document.getElementById('passStrengthBar').style.background = '#E84855';
    document.getElementById('passStrengthLabel').textContent    = '';
    ocultarError('passErrorBox');
    openModal('modal-cambiar-pass');
}

function checkPassStrength(val) {
    const bar   = document.getElementById('passStrengthBar');
    const label = document.getElementById('passStrengthLabel');
    let score = 0;
    if (val.length >= 8)           score++;
    if (/[A-Z]/.test(val))         score++;
    if (/[0-9]/.test(val))         score++;
    if (/[^A-Za-z0-9]/.test(val))  score++;
    const levels = [
        { w:'0%',   bg:'#E84855', txt:'' },
        { w:'25%',  bg:'#E84855', txt:'Débil' },
        { w:'50%',  bg:'#F59E0B', txt:'Regular' },
        { w:'75%',  bg:'#1A6FD4', txt:'Buena' },
        { w:'100%', bg:'#0F9A5A', txt:'Fuerte' },
    ];
    const lv             = levels[score];
    bar.style.width      = lv.w;
    bar.style.background = lv.bg;
    label.textContent    = lv.txt;
    label.style.color    = lv.bg;
}

function confirmarCambioPassword() {
    const nueva   = document.getElementById('newPass').value;
    const confirm = document.getElementById('confirmPass').value;

    if (!nueva || nueva.length < 8) {
        mostrarError('passErrorBox', 'La contraseña debe tener al menos 8 caracteres.'); return;
    }
    if (nueva !== confirm) {
        mostrarError('passErrorBox', 'Las contraseñas no coinciden.'); return;
    }

    nuevaPasswordPendiente = nueva;
    closeModal('modal-cambiar-pass');
    showToast('Contraseña lista. Guarda los cambios para aplicarla.', 'success');
}

async function guardarNuevoEmpleado() {
    const nombre    = document.getElementById('nuevoNombre').value.trim();
    const apellidos = document.getElementById('nuevoApellidos').value.trim();
    const email     = document.getElementById('nuevoEmail').value.trim();
    const password  = document.getElementById('nuevoPass').value;
    const tel       = document.getElementById('nuevoTel').value.trim();
    const rol       = document.getElementById('nuevoRol').value;
    const tipo      = document.getElementById('nuevoTipo').value;
    const idSede    = document.getElementById('nuevoSede').value;
    const idTurno   = document.getElementById('nuevoTurno').value;

    if (!nombre)    { mostrarError('nuevoErrorBox', 'El nombre es obligatorio.');       return; }
    if (!apellidos) { mostrarError('nuevoErrorBox', 'Los apellidos son obligatorios.'); return; }
    if (!email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
        mostrarError('nuevoErrorBox', 'Introduce un email válido.'); return;
    }
    if (!password || password.length < 8) {
        mostrarError('nuevoErrorBox', 'La contraseña debe tener al menos 8 caracteres.'); return;
    }
    if (tel && !/^\+?[\d\s\-]{7,15}$/.test(tel)) {
        mostrarError('nuevoErrorBox', 'El teléfono no tiene un formato válido.'); return;
    }

    const body = {
        idEmpresa:    parseInt(sessionStorage.getItem('empresaId')),
        nombre, apellidos, email, password, rol,
        tipoEmpleado: tipo,
        telefono:     tel || null,
        idTurno:      idTurno ? parseInt(idTurno) : null,
    };

    try {
        const nuevo = await apiFetch(`${API}/usuarios`, { method: 'POST', body: JSON.stringify(body) });
        await actualizarSede(nuevo.id, idSede);
        showToast('Empleado creado correctamente', 'success');
        closeModal('modal-empleado-nuevo');
        limpiarNuevo();
        cargarEmpleados();
    } catch (e) {
        let msg = e.message || 'Error desconocido';
        if (e.errores) msg += '<br>' + Object.entries(e.errores)
            .map(([k, v]) => `• <b>${k}:</b> ${v}`).join('<br>');
        mostrarError('nuevoErrorBox', msg);
    }
}

function limpiarNuevo() {
    ['nuevoNombre', 'nuevoApellidos', 'nuevoEmail', 'nuevoPass', 'nuevoTel'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    document.getElementById('nuevoRol').selectedIndex   = 0;
    document.getElementById('nuevoTipo').selectedIndex  = 0;
    document.getElementById('nuevoSede').selectedIndex  = 0;
    document.getElementById('nuevoTurno').selectedIndex = 0;
    ocultarError('nuevoErrorBox');
}

function abrirModalNuevo() {
    limpiarNuevo();
    openModal('modal-empleado-nuevo');
}

async function toggleActivo(id, activo) {
    try {
        await apiFetch(`${API}/usuarios/${id}/toggle-activo`, { method: 'PATCH' });
        showToast(activo ? 'Empleado desactivado' : 'Empleado activado', 'success');
        cargarEmpleados();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function actualizarSede(idUsuario, idSede) {
    const sedesActuales = await apiFetch(`${API}/empleado-sede/usuario/${idUsuario}`);
    for (const s of sedesActuales) {
        await apiFetch(`${API}/empleado-sede/usuario/${idUsuario}/sede/${s.idSede}`, { method: 'DELETE' });
    }
    if (idSede) {
        await apiFetch(`${API}/empleado-sede`, {
            method: 'POST',
            body: JSON.stringify({ idUsuario, idSede: parseInt(idSede) })
        });
    }
}

function mostrarError(boxId, msg) {
    const box = document.getElementById(boxId);
    if (!box) return;
    box.innerHTML     = msg;
    box.style.display = 'block';
}

function ocultarError(boxId) {
    const box = document.getElementById(boxId);
    if (box) { box.innerHTML = ''; box.style.display = 'none'; }
}

['filtroRolInput', 'filtroSedeInput', 'filtroEstadoInput'].forEach(id =>
    document.getElementById(id)?.addEventListener('input', renderTable)
);
document.getElementById('busqueda')?.addEventListener('input', renderTable);

cargarSedesYTurnos();
cargarEmpleados();