let todasLasSedes    = [];
let selectedSedeId   = null;
let sedeEditandoId   = null;

let mapsReady          = false;
let modalMap           = null;
let modalMarker        = null;
let modalCircle        = null;
let detailMap          = null;
let detailMarker       = null;
let detailCircle       = null;
let autocompleteWidget = null;

const DEFAULT_LAT = 40.4168;
const DEFAULT_LNG = -3.7038;

function initGoogleMaps() {
    mapsReady = true;
}

function initAutocomplete() {
    const input = document.getElementById('addrSearch');
    if (!input || !mapsReady) return;

    if (autocompleteWidget) {
        google.maps.event.clearInstanceListeners(autocompleteWidget);
    }

    autocompleteWidget = new google.maps.places.Autocomplete(input, {
        fields: ['formatted_address', 'geometry'],
        types:  ['geocode', 'establishment'],
    });

    input.addEventListener('keydown', e => {
        if (e.key === 'Enter') e.preventDefault();
    });

    autocompleteWidget.addListener('place_changed', () => {
        const place = autocompleteWidget.getPlace();

        if (!place.geometry || !place.geometry.location) {
            showToast(t('sedes.dir_no_encontrada') || 'No se encontró la dirección seleccionada', 'error');
            return;
        }

        const lat  = place.geometry.location.lat();
        const lng  = place.geometry.location.lng();
        const addr = place.formatted_address;

        document.getElementById('sedeDireccion').value = addr;
        document.getElementById('sedeLat').value       = lat.toFixed(7);
        document.getElementById('sedeLon').value       = lng.toFixed(7);

        if (modalMap) {
            moveModalMarker(lat, lng);
            modalMap.setCenter({ lat, lng });
            modalMap.setZoom(zoomForRadius(parseInt(document.getElementById('sedeRadio').value)));
        }
    });
}

async function cargarSedes() {
    try {
        todasLasSedes = await apiFetch(`${API}/sedes`);
        renderTable();
    } catch (e) {
        showToast(e.message || 'Error al cargar sedes', 'error');
    }
}

function renderTable() {
    document.querySelector('#sedesTable tbody').innerHTML = todasLasSedes.length
        ? todasLasSedes.map(s => `
      <tr class="${selectedSedeId === s.id ? 'sel' : ''}"
          onclick="selectSede(${s.id})" style="cursor:pointer">
        <td style="font-weight:600">${s.nombre}</td>
        <td>${s.direccion || '—'}</td>
        <td>—</td>
        <td><code style="font-family:var(--mono);font-size:12px">${s.radioMetros} m</code></td>
        <td>${s.activa
            ? '<span class="badge badge-green">Activa</span>'
            : '<span class="badge badge-gray">Inactiva</span>'}</td>
        <td><div class="actions">
          <button class="act-btn act-edit"
            onclick="event.stopPropagation();editarSede(${s.id})">Editar</button>
          <button class="act-btn act-delete"
            onclick="event.stopPropagation();confirmDelete('¿Eliminar esta sede?',()=>eliminarSede(${s.id}))">Eliminar</button>
        </div></td>
      </tr>`).join('')
        : `<tr><td colspan="6" style="text-align:center;padding:40px;color:var(--text-label)">
         No hay sedes registradas
       </td></tr>`;
}

function selectSede(id) {
    selectedSedeId = id;
    const s = todasLasSedes.find(x => x.id === id);
    if (!s) return;

    document.getElementById('det-nombre').textContent = s.nombre;
    document.getElementById('det-dir').textContent    = s.direccion || '—';
    document.getElementById('det-coords').textContent =
        (s.latitud != null && s.longitud != null)
            ? `${s.latitud.toFixed(6)}, ${s.longitud.toFixed(6)}`
            : '—';
    document.getElementById('det-radio').textContent = `${s.radioMetros} m`;
    document.getElementById('det-emp').textContent   = '—';

    if (s.latitud != null && s.longitud != null && mapsReady) {
        document.getElementById('mapPlaceholder').style.display = 'none';
        document.getElementById('detailMap').style.display      = 'block';
        renderDetailMap(s.latitud, s.longitud, s.radioMetros);
    } else {
        document.getElementById('mapPlaceholder').style.display = 'flex';
        document.getElementById('detailMap').style.display      = 'none';
        document.getElementById('mapPlaceholder').querySelector('span').textContent = s.nombre;
    }

    renderTable();
}

function renderDetailMap(lat, lng, radio) {
    const center = { lat, lng };
    const el     = document.getElementById('detailMap');

    if (!detailMap) {
        detailMap = new google.maps.Map(el, {
            center,
            zoom: zoomForRadius(radio),
            disableDefaultUI: true,
            zoomControl: true,
            mapTypeControl: false,
            streetViewControl: false,
            fullscreenControl: false,
        });
        detailMarker = new google.maps.Marker({ position: center, map: detailMap });
        detailCircle = new google.maps.Circle({
            map: detailMap, center, radius: radio,
            strokeColor: '#3B82F6', strokeOpacity: 0.8, strokeWeight: 2,
            fillColor: '#3B82F6', fillOpacity: 0.12,
        });
    } else {
        detailMap.setCenter(center);
        detailMap.setZoom(zoomForRadius(radio));
        detailMarker.setPosition(center);
        detailCircle.setCenter(center);
        detailCircle.setRadius(radio);
    }
}

function initModalMap(lat, lng, radio) {
    const center = { lat, lng };
    const el     = document.getElementById('modalMap');

    if (modalMap) {
        modalMap.setCenter(center);
        modalMap.setZoom(zoomForRadius(radio));
        modalMarker.setPosition(center);
        modalCircle.setCenter(center);
        modalCircle.setRadius(radio);
        return;
    }

    modalMap = new google.maps.Map(el, {
        center,
        zoom: zoomForRadius(radio),
        mapTypeControl: false,
        streetViewControl: false,
        fullscreenControl: false,
    });

    modalMarker = new google.maps.Marker({
        position: center,
        map: modalMap,
        draggable: true,
        title: 'Arrastra para mover la sede',
    });

    modalCircle = new google.maps.Circle({
        map: modalMap, center, radius: radio,
        strokeColor: '#3B82F6', strokeOpacity: 0.9, strokeWeight: 2,
        fillColor: '#3B82F6', fillOpacity: 0.15,
        editable: true, draggable: false,
    });

    modalMap.addListener('click', e => {
        moveModalMarker(e.latLng.lat(), e.latLng.lng());
    });

    modalMarker.addListener('dragend', e => {
        moveModalMarker(e.latLng.lat(), e.latLng.lng());
    });

    modalCircle.addListener('radius_changed', () => {
        syncRadioUI(Math.round(modalCircle.getRadius()));
    });
}

function moveModalMarker(lat, lng) {
    const pos = { lat, lng };
    modalMarker.setPosition(pos);
    modalCircle.setCenter(pos);
    document.getElementById('sedeLat').value = lat.toFixed(7);
    document.getElementById('sedeLon').value = lng.toFixed(7);
}

function onRadioSlider(val) {
    syncRadioUI(parseInt(val));
    if (modalCircle) modalCircle.setRadius(parseInt(val));
}

function onRadioInput(val) {
    const v = Math.max(50, Math.min(5000, parseInt(val) || 100));
    syncRadioUI(v);
    if (modalCircle) modalCircle.setRadius(v);
}

function syncRadioUI(v) {
    document.getElementById('sedeRadio').value        = v;
    document.getElementById('sedeRadioSlider').value  = v;
    document.getElementById('radioLabel').textContent = v;
    if (modalMap) modalMap.setZoom(zoomForRadius(v));
}

function syncMapFromInputs() {
    const lat = parseFloat(document.getElementById('sedeLat').value);
    const lng = parseFloat(document.getElementById('sedeLon').value);
    if (!isNaN(lat) && !isNaN(lng) && modalMap) {
        moveModalMarker(lat, lng);
        modalMap.setCenter({ lat, lng });
    }
}

function zoomForRadius(r) {
    if (r <= 100)  return 17;
    if (r <= 300)  return 16;
    if (r <= 600)  return 15;
    if (r <= 1200) return 14;
    if (r <= 2500) return 13;
    return 12;
}

function abrirModalNueva() {
    sedeEditandoId = null;
    document.getElementById('modal-sede-title').textContent = t('sedes.modal_nueva') || 'Nueva sede';
    ['sedeNombre', 'sedeDireccion', 'sedeLat', 'sedeLon', 'addrSearch'].forEach(id => {
        document.getElementById(id).value = '';
    });
    document.getElementById('sedeEstado').value = 'activa';
    syncRadioUI(100);
    openModal('modal-sede');

    setTimeout(() => {
        if (mapsReady) {
            initModalMap(DEFAULT_LAT, DEFAULT_LNG, 100);
            initAutocomplete();
        }
    }, 150);
}

function editarSede(id) {
    const s = todasLasSedes.find(x => x.id === id);
    if (!s) return;
    sedeEditandoId = id;

    document.getElementById('modal-sede-title').textContent = t('sedes.modal_editar') || 'Editar sede';
    document.getElementById('sedeNombre').value    = s.nombre;
    document.getElementById('sedeDireccion').value = s.direccion  || '';
    document.getElementById('sedeLat').value       = s.latitud    ?? '';
    document.getElementById('sedeLon').value       = s.longitud   ?? '';
    document.getElementById('sedeEstado').value    = s.activa ? 'activa' : 'inactiva';
    document.getElementById('addrSearch').value    = '';
    syncRadioUI(s.radioMetros);
    openModal('modal-sede');

    const lat = s.latitud  ?? DEFAULT_LAT;
    const lng = s.longitud ?? DEFAULT_LNG;
    setTimeout(() => {
        if (mapsReady) {
            initModalMap(lat, lng, s.radioMetros);
            initAutocomplete();
        }
    }, 150);
}

async function guardarSede() {
    const nombre    = document.getElementById('sedeNombre').value.trim();
    const direccion = document.getElementById('sedeDireccion').value.trim();
    const latitud   = parseFloat(document.getElementById('sedeLat').value);
    const longitud  = parseFloat(document.getElementById('sedeLon').value);
    const radio     = parseInt(document.getElementById('sedeRadio').value);
    const activa    = document.getElementById('sedeEstado').value === 'activa';

    if (!nombre)                           { showToast(t('sedes.err_nombre') || 'El nombre es obligatorio',        'error'); return; }
    if (isNaN(latitud) || isNaN(longitud)) { showToast(t('sedes.err_coords') || 'Selecciona una dirección válida', 'error'); return; }

    const body = {
        idEmpresa: parseInt(sessionStorage.getItem('empresaId')),
        nombre, direccion: direccion || null,
        latitud, longitud, radioMetros: radio, activa,
    };

    try {
        if (sedeEditandoId) {
            await apiFetch(`${API}/sedes/${sedeEditandoId}`, { method: 'PUT', body: JSON.stringify(body) });
            showToast(t('sedes.actualizada') || 'Sede actualizada', 'success');
        } else {
            await apiFetch(`${API}/sedes`, { method: 'POST', body: JSON.stringify(body) });
            showToast(t('sedes.creada') || 'Sede creada correctamente', 'success');
        }
        sedeEditandoId = null;
        closeModal('modal-sede');
        cargarSedes();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

async function eliminarSede(id) {
    try {
        await apiFetch(`${API}/sedes/${id}`, { method: 'DELETE' });
        if (selectedSedeId === id) {
            selectedSedeId = null;
            document.getElementById('mapPlaceholder').style.display = 'flex';
            document.getElementById('detailMap').style.display      = 'none';
            document.getElementById('mapPlaceholder').querySelector('span').textContent =
                t('sedes.selecciona') || 'Selecciona una sede';
            ['det-nombre', 'det-dir', 'det-coords', 'det-radio', 'det-emp']
                .forEach(id => document.getElementById(id).textContent = '—');
        }
        showToast(t('sedes.eliminada') || 'Sede eliminada', 'success');
        cargarSedes();
    } catch (e) {
        showToast(e.message, 'error');
    }
}

cargarSedes();