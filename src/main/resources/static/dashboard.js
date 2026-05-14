let todosLosFichajes = [];
let sedes = [];

async function cargarDashboard() {
    try {
        const [fichajes, usuarios, solicitudes, incidencias, sedesData,resumenSedes] = await Promise.all([
            apiFetch(`${API}/fichajes`),
            apiFetch(`${API}/usuarios`),
            apiFetch(`${API}/solicitudes`),
            apiFetch(`${API}/incidencias`),
            apiFetch(`${API}/sedes`),
            apiFetch(`${API}/empleado-sede/resumen-sedes`)

        ]);

        sedes = sedesData;

        todosLosFichajes = fichajes.sort(
            (a, b) => new Date(b.timestampFicha) - new Date(a.timestampFicha)
        );

        const activos = usuarios.filter(u => u.activo).length;

        document.querySelector('.stat-card:nth-child(1) .stat-value').textContent = activos;
        document.querySelector('.stat-card:nth-child(1) .stat-badge').textContent =
            `${activos} ${t('comun.activo')}`;

        const hoy = new Date().toISOString().slice(0, 10);

        const fichajesHoy = fichajes.filter(f =>
            f.timestampFicha?.startsWith(hoy)
        );

        const presentesIds = new Set();

        fichajesHoy.forEach(f => {
            if (f.tipo === 'entrada') {
                presentesIds.add(f.idUsuario);
            } else if (f.tipo === 'salida') {
                presentesIds.delete(f.idUsuario);
            }
        });

        const presentes = presentesIds.size;

        const pct = activos > 0
            ? Math.round((presentes / activos) * 100)
            : 0;

        document.querySelector('.stat-card:nth-child(2) .stat-value').textContent =
            presentes;

        document.querySelector('.stat-card:nth-child(2) .stat-badge').textContent =
            `${pct}%`;

        const pendientes = solicitudes.filter(
            s => s.estado.toLowerCase() === 'pendiente'
        ).length;

        document.querySelector('.stat-card:nth-child(3) .stat-value').textContent =
            pendientes;

        document.querySelector('.stat-card:nth-child(3) .stat-badge').textContent =
            `${pendientes} pend.`;

        const abiertas = incidencias.filter(i => !i.resuelta).length;

        document.querySelector('.stat-card:nth-child(4) .stat-value').textContent =
            abiertas;

        document.querySelector('.stat-card:nth-child(4) .stat-badge').textContent =
            `${abiertas} ${t('dashboard.hoy')}`;

        renderFichajes(todosLosFichajes);
        renderPresenciaSedes(resumenSedes);
    } catch (e) {
        showToast('Error al cargar los datos', 'error');
        console.error(e);
    }
}

function formatDateTime(iso) {
    if (!iso) return '—';

    const d = new Date(iso);

    const fecha = d.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    });

    const hora = d.toLocaleTimeString('es-ES', {
        hour: '2-digit',
        minute: '2-digit'
    });

    return `
        <span style="display:block;font-weight:600">${hora}</span>
        <span style="font-size:11px;color:var(--text-muted)">
            ${fecha}
        </span>
    `;
}

function renderFichajes(data) {
    const tbody = document.querySelector('#fichajesTable tbody');

    if (!data.length) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5"
                    style="text-align:center;padding:40px;color:var(--text-label)">
                    No hay fichajes registrados
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = data.slice(0, 10).map(f => {

        const nombre = f.nombreUsuario || '—';
        const color = avatarColor(nombre);
        const ini = initials(nombre);

        const tipoBadge = f.tipo === 'entrada'
            ? `<span class="badge badge-green">${t('comun.entrada')}</span>`
            : `<span class="badge badge-red">${t('comun.salida')}</span>`;

        const gpsBadge = f.dentroDeRadio
            ? `<span class="badge badge-green">${t('comun.dentro')}</span>`
            : `<span class="badge badge-red">${t('comun.fuera')}</span>`;

        const hora = f.timestampFicha
            ? formatDateTime(f.timestampFicha)
            : '—';

        return `
            <tr>
                <td>
                    <div class="emp-cell">
                        <div class="emp-avatar" style="background:${color}">
                            ${ini}
                        </div>
                        ${nombre}
                    </div>
                </td>
                <td>${tipoBadge}</td>
                <td>
                    <code style="font-family:var(--mono);font-size:12px">
                        ${hora}
                    </code>
                </td>
                <td>${f.nombreSede || '—'}</td>
                <td>${gpsBadge}</td>
            </tr>
        `;
    }).join('');
}

function renderPresenciaSedes(resumenSedes = []) {

    const contenedor = document.getElementById('presenciaSedes');

    if (!contenedor) return;

    const total = resumenSedes.reduce((acc, s) => acc + s.totalEmpleados, 0);

    if (total === 0) {
        contenedor.innerHTML = `<div style="color:var(--text-muted)">No hay empleados asignados</div>`;
        return;
    }

    contenedor.innerHTML = resumenSedes.map(s => {

        const pct = Math.round((s.totalEmpleados / total) * 100);

        return `
            <div class="sede-row">

                <span class="sede-name">${s.nombreSede}</span>

                <div class="sede-bar-wrap">
                    <div class="sede-bar" style="width:${pct}%"></div>
                </div>

                <span class="sede-pct">${pct}%</span>

            </div>
        `;
    }).join('');
}

document.getElementById('searchInput').addEventListener('input', function () {
    const q = this.value.toLowerCase();

    const filtered = todosLosFichajes.filter(f =>
        (f.nombreUsuario || '').toLowerCase().includes(q) ||
        (f.nombreSede || '').toLowerCase().includes(q)
    );

    renderFichajes(filtered);
});

cargarDashboard();