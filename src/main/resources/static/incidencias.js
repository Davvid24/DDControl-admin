
let todasLasIncidencias = [];

async function cargarIncidencias() {
  try {
    todasLasIncidencias = await apiFetch(`${API}/incidencias`);
    renderTable();
  } catch (e) {
    showToast(e.message || 'Error al cargar incidencias', 'error');
  }
}

function renderTable() {
  const abiertas = todasLasIncidencias.filter(i => !i.resuelta);

  const cnt = document.getElementById('cnt-pendientes');
  if (cnt) cnt.textContent = abiertas.length;

  document.querySelector('#incidenciasTable tbody').innerHTML = abiertas.length
    ? abiertas.map(i => {
        const nombre = i.nombreUsuario || '—';
        const color  = avatarColor(nombre);
        const ini    = initials(nombre);

        const tipoBadgeClass = i.tipo === 'gps'    ? 'badge-red'
          : i.tipo === 'olvido'  ? 'badge-yellow'
          : i.tipo === 'retraso' ? 'badge-yellow'
          : 'badge-gray';

        return `<tr>
          <td><span class="badge ${tipoBadgeClass}">${i.tipo}</span></td>
          <td><div class="emp-cell">
            <div class="emp-avatar" style="background:${color}">${ini}</div>
            ${nombre}
          </div></td>
          <td style="color:var(--text-muted);max-width:300px">${i.descripcion || '—'}</td>
          <td><code style="font-family:var(--mono);font-size:11px">${formatDateTime(i.fecha)}</code></td>
          <td><span class="badge badge-red">Abierta</span></td>
          <td><div class="actions">
            <button class="act-btn act-approve" onclick="resolver(${i.id})">Resolver</button>
          </div></td>
        </tr>`;
      }).join('')
    : `<tr><td colspan="6" style="text-align:center;padding:48px;color:var(--text-label);font-size:15px">
         🎉 No hay incidencias abiertas
       </td></tr>`;
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
