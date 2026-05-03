let todosLosFichajes = [];

async function cargarDashboard() {
    try {
        const [fichajes, usuarios, solicitudes, incidencias] = await Promise.all([
            apiFetch(`${API}/fichajes`),
            apiFetch(`${API}/usuarios`),
            apiFetch(`${API}/solicitudes`),
            apiFetch(`${API}/incidencias`)
        ]);

        todosLosFichajes = fichajes;

        // Empleados activos
        const activos = usuarios.filter(u => u.activo).length;
        document.querySelector('.stat-card:nth-child(1) .stat-value').textContent = activos;
        document.querySelector('.stat-card:nth-child(1) .stat-badge').textContent = `${activos} activos`;

        // Presentes hoy
        const hoy = new Date().toISOString().slice(0, 10);
        const fichajesHoy = fichajes.filter(f => f.timestampFicha?.startsWith(hoy));
        const presentesIds = new Set();
        fichajesHoy.forEach(f => {
            if (f.tipo === 'entrada') presentesIds.add(f.idUsuario);
            else if (f.tipo === 'salida') presentesIds.delete(f.idUsuario);
        });
        const presentes = presentesIds.size;
        const pct = activos > 0 ? Math.round(presentes / activos * 100) : 0;
        document.querySelector('.stat-card:nth-child(2) .stat-value').textContent = presentes;
        document.querySelector('.stat-card:nth-child(2) .stat-badge').textContent = `${pct}%`;

        // Solicitudes pendientes
        const pendientes = solicitudes.filter(s => s.estado.toLowerCase() === 'pendiente').length;
        document.querySelector('.stat-card:nth-child(3) .stat-value').textContent = pendientes;
        document.querySelector('.stat-card:nth-child(3) .stat-badge').textContent = `${pendientes} pend.`;

        // Incidencias abiertas
        const abiertas = incidencias.filter(i => !i.resuelta).length;
        document.querySelector('.stat-card:nth-child(4) .stat-value').textContent = abiertas;
        document.querySelector('.stat-card:nth-child(4) .stat-badge').textContent = `${abiertas} hoy`;

        renderFichajes(todosLosFichajes);
    } catch (e) {
        showToast('Error al cargar los datos', 'error');
    }
}

function renderFichajes(data) {
    const tbody = document.querySelector('#fichajesTable tbody');
    if (!data.length) {
        tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;padding:40px;color:var(--text-label)">No hay fichajes registrados</td></tr>`;
        return;
    }
    tbody.innerHTML = data.slice(0, 10).map(f => {
        const nombre    = f.nombreUsuario || '—';
        const color     = avatarColor(nombre);
        const ini       = initials(nombre);
        const tipoBadge = f.tipo === 'entrada'
            ? '<span class="badge badge-green">Entrada</span>'
            : '<span class="badge badge-red">Salida</span>';
        const gpsBadge  = f.dentroDeRadio
            ? '<span class="badge badge-green">✓ Dentro</span>'
            : '<span class="badge badge-red">✗ Fuera</span>';
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
    </tr>`;
    }).join('');
}

document.getElementById('searchInput').addEventListener('input', function () {
    const q = this.value.toLowerCase();
    const filtered = todosLosFichajes.filter(f =>
        (f.nombreUsuario || '').toLowerCase().includes(q) ||
        (f.nombreSede    || '').toLowerCase().includes(q)
    );
    renderFichajes(filtered);
});

cargarDashboard();