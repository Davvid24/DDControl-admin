

async function generarInforme() {
  const desde    = document.getElementById('filtroDesde').value;
  const hasta    = document.getElementById('filtroHasta').value;
  const idUsuario = document.getElementById('filtroEmp').value;
  const idSede    = document.getElementById('filtroSede').value;

  if (!desde || !hasta) { showToast('Selecciona un período', 'error'); return; }

  try {
    let url = `${API}/fichajes`;
    if (idUsuario) url = `${API}/fichajes/usuario/${idUsuario}`;

    const fichajes = await apiFetch(url);

    const desdeTs = new Date(desde).getTime();
    const hastaTs = new Date(hasta + 'T23:59:59').getTime();
    const filtered = fichajes.filter(f => {
      const ts = new Date(f.timestampFicha).getTime();
      return ts >= desdeTs && ts <= hastaTs &&
        (!idSede || String(f.idSede) === idSede);
    });

    renderInforme(filtered);
    showToast('Informe generado correctamente', 'success');
  } catch (e) {
    showToast(e.message || 'Error al generar informe', 'error');
  }
}

function renderInforme(fichajes) {
  const porUsuario = {};
  fichajes.forEach(f => {
    const key = f.nombreUsuario || `Usuario ${f.idUsuario}`;
    if (!porUsuario[key]) porUsuario[key] = [];
    porUsuario[key].push(f);
  });

  const rows = Object.entries(porUsuario).map(([nombre, fiches]) => {
    const color = avatarColor(nombre);
    const ini   = initials(nombre);

    let totalMin = 0;
    const entradas = fiches.filter(f => f.tipo === 'entrada').sort((a,b) =>
      new Date(a.timestampFicha) - new Date(b.timestampFicha));
    const salidas  = fiches.filter(f => f.tipo === 'salida').sort((a,b) =>
      new Date(a.timestampFicha) - new Date(b.timestampFicha));

    entradas.forEach((e, i) => {
      if (salidas[i]) {
        const diff = new Date(salidas[i].timestampFicha) - new Date(e.timestampFicha);
        totalMin += Math.round(diff / 60000);
      }
    });

    const horas  = Math.floor(totalMin / 60);
    const minutos = totalMin % 60;
    const totalStr = `${horas}h ${String(minutos).padStart(2,'0')}m`;
    const diffMin  = totalMin - 480;
    const diffBadge = diffMin > 0
      ? `<span class="badge badge-green">+${Math.floor(diffMin/60)}h ${diffMin%60}m</span>`
      : diffMin < 0
        ? `<span class="badge badge-red">-${Math.floor(Math.abs(diffMin)/60)}h ${Math.abs(diffMin)%60}m</span>`
        : `<span class="badge badge-gray">0h 00m</span>`;

    return `<tr>
      <td><div class="emp-cell">
        <div class="emp-avatar" style="background:${color}">${ini}</div>
        ${nombre}
      </div></td>
      <td colspan="5" style="color:var(--text-muted)">${fiches.length} registros</td>
      <td style="font-weight:700">${totalStr}</td>
      <td>${diffBadge}</td>
    </tr>`;
  });

  document.querySelector('#informesTable tbody').innerHTML = rows.length
    ? rows.join('')
    : `<tr><td colspan="8" style="text-align:center;padding:40px;color:var(--text-label)">
         Genera un informe para ver los datos
       </td></tr>`;
}


async function cargarSelectores() {
  try {
    const [usuarios, sedes] = await Promise.all([
      apiFetch(`${API}/usuarios`),
      apiFetch(`${API}/sedes`)
    ]);
    const selEmp  = document.getElementById('filtroEmp');
    const selSede = document.getElementById('filtroSede');
    if (selEmp)  selEmp.innerHTML  = '<option value="">Todos los empleados</option>' +
      usuarios.map(u => `<option value="${u.id}">${u.nombre} ${u.apellidos}</option>`).join('');
    if (selSede) selSede.innerHTML = '<option value="">Todas las sedes</option>' +
      sedes.map(s => `<option value="${s.id}">${s.nombre}</option>`).join('');
  } catch {  }
}

cargarSelectores();
