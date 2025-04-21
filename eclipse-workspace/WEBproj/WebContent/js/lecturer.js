

	/*---------------------
	 |	 HEADER DISPLAY	  |
	 ----------------------*/
	 
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
        console.error("kys json:", e.getMessage());
      }
    })
	.catch(e => console.error("Errore whilst tryina fetch lecturer infos @lecturers.js:", e));
	}
);

	
	/*------------------------------
	 |	 DEFAULT CONTENT DISPLAY   |
	 ------------------------------*/
 
 document.addEventListener('DOMContentLoaded', () => {
   fetch('api/courses')
     .then(res => res.json())
	 .then(corsi => {
	   const corsoSelect = document.getElementById('corsoSelect');
	   corsoSelect.innerHTML = '';

		//default option [Selez. corso]:
	   const defaultOpt = document.createElement('option');
	   defaultOpt.textContent = 'Seleziona corso';
	   defaultOpt.disabled = true;
	   defaultOpt.selected = true;
	   corsoSelect.appendChild(defaultOpt);
		//fetch others
	   corsi.forEach(corso => {
	     const opt = document.createElement('option');
	     opt.value = corso.id;
	     opt.textContent = corso.nome;
	     corsoSelect.appendChild(opt);
	   });

	   // disable appelloSelect until course selected
	   document.getElementById('appelloSelect').disabled = true;
	 });

     });

   document.getElementById('corsoSelect').addEventListener('change', () => {
     const corsoId = document.getElementById('corsoSelect').value;
	 fetch('api/exams?corsoId=' + corsoId)
	   .then(res => res.json())
	   .then(appelli => {
	     const appelloSelect = document.getElementById('appelloSelect');
	     appelloSelect.disabled = false;
	     appelloSelect.innerHTML = '';

	     //default option [Seleziona data]:
	     const defaultOpt = document.createElement('option');
	     defaultOpt.textContent = 'Seleziona data';
	     defaultOpt.disabled = true;
	     defaultOpt.selected = true;
	     appelloSelect.appendChild(defaultOpt);

	     appelli.forEach(appello => {
	       const opt = document.createElement('option');
	       opt.value = appello.id;
	       opt.textContent = appello.data;
	       appelloSelect.appendChild(opt);
	 });
});

	/*----------------------------
	 |	 FETCH CONTENT DISPLAY   |
	 ----------------------------*/
 
   document.getElementById('appelloSelect').addEventListener('change', () => {
     const appelloId = document.getElementById('appelloSelect').value;
     fetch('api/iscritti?appelloId=' + appelloId)
       .then(res => res.json())
 	  .then(iscritti => {
 	    const tbody = document.getElementById('tabellaIscritti');
 	    tbody.innerHTML = '';
 	    iscritti.forEach(isc => {
 	      const row = document.createElement('tr');
 	      row.innerHTML = `
 	        <td>${isc.idStudent}</td>
 	        <td>${isc.name}</td>
 	        <td>${isc.surname}</td>
 	        <td>${isc.email}</td>
 	        <td>${isc.grade || ""}</td>
 	        <td>${isc.status}</td>
 	      `;
 	      tbody.appendChild(row);
 	    });

 		//then sort? qonpomxa
 	    makeSortable(tbody.closest("table"));
 	  });
   });
 });
 
 
 
 // Tryina sort somehow:
 const gradeOrder = [
   "",
   "assente",
   "rimandato",
   "riprovato",
   //numbers:
   ...Array.from({length: 13}, (_,i) => String(18 + i)),
   "30 e lode"
 ];

 function getCellValue(row, idx) {
   return row.children[idx].textContent.trim().toLowerCase();
 }

 /** 
  * comparator for a given column index and key
  */
 function comparer(idx, key, asc) {
   return (a, b) => {
     let v1 = getCellValue(a, idx);
     let v2 = getCellValue(b, idx);

     // If sorting by grade, use gradeOrder index
     if (key === "grade") {
       v1 = gradeOrder.indexOf(v1);
       v2 = gradeOrder.indexOf(v2);
     }

     // Try numeric compare first
     const n1 = parseFloat(v1), n2 = parseFloat(v2);
     if (!isNaN(n1) && !isNaN(n2)) {
       return asc ? n1 - n2 : n2 - n1;
     }
     // Otherwise string compare
     if (v1 < v2) return asc ? -1 : 1;
     if (v1 > v2) return asc ? 1 : -1;
     return 0;
   };
 }
 
 
/*https://www.youtube.com/watch?v=av5wFcAtuEI
https://stackoverflow.com/questions/55462632/javascript-sort-table-column-on-click
https://www.reddit.com/r/AskTechnology/comments/a88u9q/absolutely_simplest_way_to_create_a_sortable/?rdt=53649
https://phuoc.ng/collection/html-dom/sort-a-table-by-clicking-its-headers/
 */
 function makeSortable(table) {
   const headers = table.querySelectorAll("th");
   const tbody = table.tBodies[0];
   const sortState = {}; // remember asc/desc per key

   headers.forEach((th, idx) => {
     const key = th.dataset.key;
     if (!key) return;

     th.style.cursor = "pointer";
     th.addEventListener("click", () => {
       const asc = !sortState[key];      // toggle
       sortState[key] = asc;

       // extract rows
       const rows = Array.from(tbody.querySelectorAll("tr"));
       // sort them
       rows.sort(comparer(idx, key, asc));
       // re‑append in new order
       rows.forEach(r => tbody.appendChild(r));
     });
   });
 }






