document.addEventListener('DOMContentLoaded', () => {
  fetch('api/corsi')
    .then(res => res.json())
    .then(corsi => {
      const select = document.getElementById('corsoSelect');
      corsi.forEach(corso => {
        const opt = document.createElement('option');
        opt.value = corso.id;
        opt.textContent = corso.nome;
        select.appendChild(opt);
      });
    });

  document.getElementById('corsoSelect').addEventListener('change', () => {
    const corsoId = document.getElementById('corsoSelect').value;
    fetch('api/appelli?corsoId=' + corsoId)
      .then(res => res.json())
      .then(appelli => {
        const select = document.getElementById('appelloSelect');
        select.innerHTML = '';
        appelli.forEach(appello => {
          const opt = document.createElement('option');
          opt.value = appello.id;
          opt.textContent = appello.data;
          select.appendChild(opt);
        });
      });
  });

  document.getElementById('appelloSelect').addEventListener('change', () => {
    const appelloId = document.getElementById('appelloSelect').value;
    fetch('api/iscritti?appelloId=' + appelloId)
      .then(res => res.json())
      .then(iscritti => {
        const tbody = document.getElementById('tabellaIscritti');
        tbody.innerHTML = '';
        iscritti.forEach(isc => {
          const row = `<tr><td>${isc.matricola}</td><td>${isc.nome}</td><td>${isc.cognome}</td><td>${isc.email}</td></tr>`;
          tbody.innerHTML += row;
        });
      });
  });
});
