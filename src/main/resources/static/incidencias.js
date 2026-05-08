let todasLasIncidencias = [];
let viendoHistorial = false;

async function cargarIncidencias() {
    try {
        todasLasIncidencias = await apiFetch(`${API}/incidencias`);
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar incidencias', 'error');
    }
}

function renderTable() {
    const abiertas  = todasLasIncidencias.filter(i => !i.resuelta);
    const resueltas = todasLasIncidencias.filter(i =>  i.resuelta);

    const cnt = document.getElementById('cnt-pendientes');
    if (cnt) cnt.textContent = abiertas.length;

    const btnHistorial = document.getElementById('btn-historial');
    if (btnHistorial) {
        btnHistorial.textContent = viendoHistorial ? '← Volver a abiertas' : `Ver historial (${resueltas.length})`;
    }

    const data = viendoHistorial
        ? [...resueltas].sort((a, b) => new Date(b.fecha) - new Date(a.fecha))
        : abiertas;

    const thead = document.querySelector('#incidenciasTable thead tr');
    if (thead) {
        const ultimaCol = thead.querySelector('th:last-child');
        if (ultimaCol) ultimaCol.textContent = viendoHistorial ? 'Estado' : 'Acciones';
    }

    document.querySelector('#incidenciasTable tbody').innerHTML = data.length
        ? data.map(i => {
            const nombre = i.nombreUsuario || '—';
            const color  = avatarColor(nombre);
            const ini    = initials(nombre);

            const tipoBadgeClass = i.tipo === 'gps'    ? 'badge-red'
                : i.tipo === 'olvido'  ? 'badge-yellow'
                    : i.tipo === 'retraso' ? 'badge-yellow'
                        : 'badge-gray';

            const estadoBadge = i.resuelta
                ? '<span class="badge badge-green">Resuelta</span>'
                : '<span class="badge badge-red">Abierta</span>';

            const ultimaCol = viendoHistorial
                ? `<td>${estadoBadge}</td>`
                : `<td><div class="actions">
               <button class="act-btn act-approve" onclick="resolver(${i.id})">${t('accion.resolver')}</button>
             </div></td>`;

            return `<tr>
          <td><span class="badge ${tipoBadgeClass}">${i.tipo}</span></td>
          <td><div class="emp-cell">
            <div class="emp-avatar" style="background:${color}">${ini}</div>
            ${nombre}
          </div></td>
          <td style="color:var(--text-muted);max-width:300px">${i.descripcion || '—'}</td>
          <td><code style="font-family:var(--mono);font-size:11px">${formatDateTime(i.fecha)}</code></td>
          <td><span class="badge badge-red" ${i.resuelta ? 'style="display:none"' : ''}>Abierta</span></td>
          ${ultimaCol}
        </tr>`;
        }).join('')
        : `<tr><td colspan="6" style="text-align:center;padding:48px;color:var(--text-label);font-size:15px">
         ${viendoHistorial ? 'No hay incidencias resueltas' : 'No hay incidencias abiertas'}
       </td></tr>`;
}

function toggleHistorial() {
    viendoHistorial = !viendoHistorial;
    renderTable();
}

async function resolver(id) {
    try {
        await apiFetch(`${API}/incidencias/${id}/resolver`, { method: 'PATCH' });
        showToast('Incidencia marcada como resuelta', 'success');
        cargarIncidencias();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

cargarIncidencias();