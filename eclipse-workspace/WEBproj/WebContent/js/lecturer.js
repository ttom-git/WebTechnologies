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
          const row = `<tr><td>${isc.idStudent}</td><td>${isc.name}</td><td>${isc.surname}</td><td>${isc.email}</td></tr>`;
          tbody.innerHTML += row;
        });
      });
  });
});


/*document.addEventListener('DOMContentLoaded', () => {
  fetch("getUserInfo")
    .then(res => res.json())
    .then(data => {
      console.log("Raw fetch:", data);
	  document.getElementById("welcomeText").textContent = `Benvenuto, ${data.name} ${data.surname}`;

    })
    .catch(err => console.error("Error while fetching student's infos @student.js:", err));	  
});*/

document.addEventListener('DOMContentLoaded', () => {
  fetch("getUserInfo")
    .then(res => res.text())
    .then(txt => {
      console.log("── RAW RESPONSE ──\n", txt);
      try {	//tryin to parse it
        const data = JSON.parse(txt);
        console.log("Raw:", data);
		document.getElementById("welcomeText").textContent = `Benvenuto, ${data.name} ${data.surname}`;

      } catch (e) {
        console.error("json esploso:", e.getMessage());
      }
    })
	.catch(e => console.error("Errore whilst tryina fetch lecturer infos @lecturers.js:", e));
	}
);




