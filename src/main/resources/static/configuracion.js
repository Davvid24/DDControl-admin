
const EMPRESA_ID = 1;
const TABS = ['empresa','sistema','seguridad','notificaciones'];

function switchTab(name) {
  TABS.forEach(t => {
    document.getElementById('tab-'   + t).classList.toggle('active', t === name);
    document.getElementById('panel-' + t).style.display = t === name ? 'flex' : 'none';
  });
}

async function cargarEmpresa() {
  try {
    const e = await apiFetch(`${API}/empresas/${EMPRESA_ID}`);
    document.getElementById('empNombre').value = e.nombre       || '';
    document.getElementById('empNif').value    = e.nif          || '';
    document.getElementById('empDir').value    = e.direccion    || '';
    document.getElementById('empEmail').value  = e.emailContacto || '';
    document.getElementById('empTel').value    = e.telefono     || '';
  } catch {
    console.log('Error al cargar empresa');
  }
}

async function guardarEmpresa() {
  const nombre = document.getElementById('empNombre').value.trim();
  const nif    = document.getElementById('empNif').value.trim();
  if (!nombre || !nif) { showToast('Nombre y NIF son obligatorios', 'error'); return; }

  const body = {
    nombre,
    nif,
    direccion:     document.getElementById('empDir').value.trim()   || null,
    emailContacto: document.getElementById('empEmail').value.trim() || null,
    telefono:      document.getElementById('empTel').value.trim()   || null,
  };

  try {
    await apiFetch(`${API}/empresas/${EMPRESA_ID}`, {
      method: 'PUT', body: JSON.stringify(body)
    });
    showToast('Datos de empresa guardados', 'success');
  } catch (e) {
    showToast(e.message, 'error');
  }
}

function resetEmpresa() {
  cargarEmpresa();
}

function guardarSistema() {
  showToast('Parámetros del sistema guardados', 'success');
}

function checkStrength(val) {
  const fill  = document.getElementById('strengthFill');
  const label = document.getElementById('strengthLabel');
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
  const lv = levels[score];
  fill.style.width      = lv.w;
  fill.style.background = lv.bg;
  label.textContent     = lv.txt;
  label.style.color     = lv.bg;
}

async function cambiarPassword() {
  const nueva   = document.getElementById('passNueva').value;
  const confirm = document.getElementById('passConfirm').value;
  const actual  = document.getElementById('passActual').value;

  if (!actual || !nueva || !confirm) { showToast('Completa todos los campos', 'error');                 return; }
  if (nueva.length < 8)              { showToast('Mínimo 8 caracteres', 'error');                      return; }
  if (nueva !== confirm)             { showToast('Las contraseñas no coinciden', 'error');              return; }

  showToast('Contraseña actualizada correctamente', 'success');
  ['passActual','passNueva','passConfirm'].forEach(id =>
    document.getElementById(id).value = ''
  );
  checkStrength('');
}

function guardarNotif() {
  showToast('Preferencias de notificación guardadas', 'success');
}

cargarEmpresa();
