
let todasLasSolicitudes = [];
let estadoFiltro = 'pendiente';
let resolucionPendiente = null;

async function cargarSolicitudes() {
    try {
        todasLasSolicitudes = await apiFetch(`${API}/solicitudes`);
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar solicitudes', 'error');
    }
}

function filterEstado(estado, tabEl) {
    estadoFiltro = estado.toLowerCase();  // ← lowercase
    activateTab(tabEl);
    renderTable();
}


function renderTable() {
    const data = estadoFiltro
        ? todasLasSolicitudes.filter(s => s.estado.toLowerCase() === estadoFiltro)
        : todasLasSolicitudes;

    document.querySelector('#solicitudesTable tbody').innerHTML = data.length
        ? data.map(s => {
            const nombre = s.nombreUsuario || '—';
            const color  = avatarColor(nombre);
            const ini    = initials(nombre);

            const tipoBadgeClass = s.tipo === 'VACACIONES' ? 'badge-blue'
                : s.tipo === 'BAJA'    ? 'badge-yellow'
                    : 'badge-indigo';

            const periodo = `${formatDate(s.fechaInicio)} – ${formatDate(s.fechaFin)}`;

            const estadoBadge = s.estado.toLowerCase() === 'pendiente'
                ? '<span class="badge badge-yellow">Pendiente</span>'
                : s.estado.toLowerCase() === 'aprobada'
                    ? '<span class="badge badge-green">Aprobada</span>'
                    : '<span class="badge badge-red">Denegada</span>';

            const acciones = s.estado.toLowerCase() === 'pendiente'
                ? `<div class="actions">
         <button class="act-btn act-approve" onclick="iniciarResolucion(${s.id},'APROBADA')">Aprobar</button>
         <button class="act-btn act-deny"    onclick="iniciarResolucion(${s.id},'RECHAZADA')">Denegar</button>
       </div>`
                : estadoBadge;

            return `<tr>
          <td><div class="emp-cell">
            <div class="emp-avatar" style="background:${color}">${ini}</div>
            ${nombre}
          </div></td>
          <td><span class="badge ${tipoBadgeClass}">${s.tipo}</span></td>
          <td>${periodo}</td>
          <td style="color:var(--text-muted)">${s.motivo || '—'}</td>
          <td>${formatDate(s.fechaSolicitud)}</td>
          <td>${s.comentarioResolucion || '—'}</td>
          <td>${acciones}</td>
        </tr>`;
        }).join('')
        : `<tr><td colspan="7" style="text-align:center;padding:40px;color:var(--text-label)">
         No hay solicitudes en esta categoría
       </td></tr>`;

    const cnt = todasLasSolicitudes.filter(s => s.estado.toLowerCase() === 'pendiente').length;
    const el  = document.getElementById('cnt-pendiente');
    if (el) el.textContent = `(${cnt})`;
}

function iniciarResolucion(id, tipo) {
    resolucionPendiente = { id, tipo };
    document.getElementById('modal-res-title').textContent =
        tipo === 'APROBADA' ? 'Aprobar solicitud' : 'Denegar solicitud';
    const btn = document.getElementById('btnConfirmarResolucion');
    btn.className   = tipo === 'APROBADA' ? 'btn btn-success' : 'btn btn-danger';
    btn.textContent = tipo === 'APROBADA' ? 'Confirmar aprobación' : 'Confirmar denegación';
    document.getElementById('comentarioAdmin').value = '';
    openModal('modal-resolucion');
}

document.getElementById('btnConfirmarResolucion').addEventListener('click', async () => {
    if (!resolucionPendiente) return;
    const { id, tipo } = resolucionPendiente;
    const comentario   = document.getElementById('comentarioAdmin').value.trim();
    const idAdmin      = parseInt(sessionStorage.getItem('userId'));

    const body = {
        idAdminRevisor:       idAdmin,
        estado:               tipo,
        comentarioResolucion: comentario || null
    };

    try {
        await apiFetch(`${API}/solicitudes/${id}/resolver`, {
            method: 'PATCH',
            body:   JSON.stringify(body)
        });
        closeModal('modal-resolucion');
        showToast(
            tipo === 'APROBADA' ? 'Solicitud aprobada' : 'Solicitud denegada',
            tipo === 'APROBADA' ? 'success' : 'error'
        );
        resolucionPendiente = null;
        cargarSolicitudes();
    } catch (e) {
        showToast(e.message, 'error');
    }
});

cargarSolicitudes();