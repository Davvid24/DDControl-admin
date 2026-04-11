
let todosLosEmpleados = [];
let empleadoEditandoId = null;

async function cargarEmpleados() {
  try {
    todosLosEmpleados = await apiFetch(`${API}/usuarios`);
    renderTable();
  } catch (e) {
    showToast(e.message || 'Error al cargar empleados', 'error');
  }
}

function estadoBadge(activo) {
  return activo
    ? '<span class="badge badge-green"><span class="dot dot-green"></span>Activo</span>'
    : '<span class="badge badge-gray">Inactivo</span>';
}

function renderTable() {
  const filtroRol  = document.getElementById('filtroRol').value;
  const filtroSede = document.getElementById('filtroSede').value;

  const data = todosLosEmpleados.filter(e => {
    if (filtroRol  && e.rol              !== filtroRol)  return false;
    if (filtroSede && e.nombreEmpresa    !== filtroSede) return false;
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
          <td>${e.nombreEmpresa || '—'}</td>
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
         No hay empleados registrados
       </td></tr>`;
}

function editarEmpleado(id) {
  const e = todosLosEmpleados.find(x => x.id === id);
  if (!e) return;
  empleadoEditandoId = id;
  document.getElementById('modal-emp-title').textContent = 'Editar empleado';
  document.getElementById('empNombre').value    = e.nombre;
  document.getElementById('empApellidos').value = e.apellidos;
  document.getElementById('empEmail').value     = e.email;
  document.getElementById('empTel').value       = e.telefono || '';
  document.getElementById('empRol').value       = e.rol;
  document.getElementById('empTipo').value      = e.tipoEmpleado;
  document.getElementById('empPass').value      = '';  // no se muestra
  openModal('modal-empleado');
}

async function toggleActivo(id, activo) {
  try {
    await apiFetch(`${API}/usuarios/${id}/toggle-activo`, { method: 'PATCH' });
    showToast(activo ? 'Empleado desactivado' : 'Empleado activado', 'info');
    cargarEmpleados();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

async function guardarEmpleado() {
  const nombre    = document.getElementById('empNombre').value.trim();
  const apellidos = document.getElementById('empApellidos').value.trim();
  const email     = document.getElementById('empEmail').value.trim();
  const password  = document.getElementById('empPass').value;
  const rol       = document.getElementById('empRol').value;
  const tipo      = document.getElementById('empTipo').value;
  const tel       = document.getElementById('empTel').value.trim();

  if (!nombre || !apellidos || !email) {
    showToast('Nombre, apellidos y email son obligatorios', 'error'); return;
  }
  if (!empleadoEditandoId && !password) {
    showToast('La contraseña es obligatoria para nuevos empleados', 'error'); return;
  }

  const body = {
    idEmpresa:  parseInt(sessionStorage.getItem('idEmpresa')),
    nombre, apellidos, email, rol,
    tipoEmpleado: tipo,
    telefono:     tel || null,
    password:     password || undefined
  };

  try {
    if (empleadoEditandoId) {
      await apiFetch(`${API}/usuarios/${empleadoEditandoId}`, {
        method: 'PUT', body: JSON.stringify(body)
      });
      showToast('Empleado actualizado', 'success');
    } else {
      await apiFetch(`${API}/usuarios`, {
        method: 'POST', body: JSON.stringify(body)
      });
      showToast('Empleado creado correctamente', 'success');
    }
    empleadoEditandoId = null;
    closeModal('modal-empleado');
    cargarEmpleados();
  } catch (e) {
    showToast(e.message, 'error');
  }
}

function abrirModalNuevo() {
  empleadoEditandoId = null;
  document.getElementById('modal-emp-title').textContent = 'Nuevo empleado';
  ['empNombre','empApellidos','empEmail','empPass','empTel','empDepto'].forEach(id => {
    const el = document.getElementById(id);
    if (el) el.value = '';
  });
  openModal('modal-empleado');
}

['filtroRol','filtroSede'].forEach(id =>
  document.getElementById(id)?.addEventListener('change', renderTable)
);

cargarEmpleados();
