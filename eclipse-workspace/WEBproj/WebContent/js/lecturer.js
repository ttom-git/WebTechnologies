
let currentExamId = null; 	//this should make it global or smth like that

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
		
		//then updates publishable results
		updatePublishableResults();
 	  });
   });
 });
 
 
 
 // Tryina sort somehow:
 const gradeOrder = [
   "",
   " ",
   "assente",
   "rimandato",
   "riprovato",
   "18",
   "19",
   "20",
   "21",
   "22",
   "23",
   "24",
   "25",
   "26",
   "27",
   "28",
   "29",
   "30",
   "30 e lode"
 ];
 

 function getCellValue(row, idx) {
   return row.children[idx].textContent.trim().toLowerCase();
 }

 // comparator for a given column index and key
 function comparer(idx, key, asc) {
   return (a, b) => {
     let v1 = getCellValue(a, idx);
     let v2 = getCellValue(b, idx);

     // If sorting by grade, use gradeOrder index
     if (key == "grade") {
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
 
 

 /*-------------------
  |	 SORTING METHOD  |
  -------------------*/
 
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

       const rows = Array.from(tbody.querySelectorAll("tr"));
       rows.sort(comparer(idx, key, asc));
       rows.forEach(r => tbody.appendChild(r));		   // reappending in new order the (now sorted) rows

     });
   });
 }


 
 /*----------------------------
  |	 SINGLE-MULTIPLE INSERT   |
  ----------------------------*/

  const insertModal = document.getElementById('insertModal');	
  insertModal.style.display = 'none';			//hiding mode initally

  const insertBtn = document.getElementById('insertBtn');
  insertBtn.disabled = true;	//should keep button disabled until rows are loaded 

  // --------	apppelloSelect handler :	-------------
  document.getElementById('appelloSelect').addEventListener('change', () => {
	const appelloId = document.getElementById('appelloSelect').value;
	currentExamId = appelloId; 	// storing appelloId in session for InserResultServlet 

	 fetch('StoreExamId', {
	   method: 'POST',
	   headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	   body: `examId=${encodeURIComponent(appelloId)}`
	 });
	 insertBtn.disabled = false;
  });


  insertBtn.addEventListener('click', () => {
    const rows = Array.from(document.querySelectorAll('#tabellaIscritti tr'));
    const body = document.getElementById('tableBody');
    body.innerHTML = '';

    rows.forEach(tr => {
      const status = tr.children[5].textContent.trim().toLowerCase();
      if (status == 'pending' || status == 'added') { 	// also 'added' cosi che si possa modificare quelli non ancora pubblicati :)  //TODO maybe add a 'modifica' button 
        const [mat, name, surname, status] = [	//nome cognome maybe unecessary when adding new results: matricola dovrehhe bastare
          tr.children[0].textContent,
          tr.children[1].textContent,
          tr.children[2].textContent,
		  tr.children[5].textContent.trim().toLowerCase()
        ];

        const select = document.createElement('select');
        select.name = 'vote';
        select.dataset.matricola = mat;
        select.innerHTML = `
          <option value="">– seleziona –</option>
          <option value="absent">assente</option>
          <option value="rejected">rimandato</option>
          <option value="retried">riprovato</option>
		  <option value="18">18</option>
		  <option value="19">19</option>
		  <option value="20">20</option>
		  <option value="21">21</option>
		  <option value="22">22</option>
		  <option value="23">23</option>
		  <option value="24">24</option>
		  <option value="25">25</option>
		  <option value="26">26</option>
		  <option value="27">27</option>
		  <option value="28">28</option>
		  <option value="29">29</option>
		  <option value="30">30</option>
          <option value="laude">30 e lode</option>
        `;

        const tr2 = document.createElement('tr');
        tr2.innerHTML = `
			<td>${mat}</td>
			<td>${name}</td>
			<td>${surname}</td>
			<td></td>
			<td>${status}</td>`;
        tr2.children[3].appendChild(select);
        body.appendChild(tr2);
      }
    });
    insertModal.style.display = 'flex';
  });
  //cancel button  
  document.getElementById('cancel').onclick = () => {
    insertModal.style.display = 'none';
  };
  //submitButton
    document.getElementById('form').addEventListener('submit', e => {
    e.preventDefault(); 	//leave this so it doesnt crashes back - 24 Apr

    const inputs = Array.from(
      document.querySelectorAll('#tableBody select[name="vote"]')
    );

    const payload = inputs
      .filter(sel => sel.value != "")   // only those filled out 
      .map(sel => ({
        idStudente: sel.dataset.matricola,
        grade:      sel.value
      }));

    if (payload.length == 0) {
      alert("Devi scegliere almeno un voto >:C");
      return;
    }
    /*fetch('InsertResults', {
      method: 'POST',
      headers: {'Content-Type':'application/json'},
      body: JSON.stringify(payload)
    })*/
	fetch('InsertResults', {
	  method: 'POST',
	  credentials: 'include',           // <<< mega important so that session is sent( + cookies?)	[for idExam in InsertResultServlet]
	  headers: { 'Content-Type': 'application/json' },
	  body: JSON.stringify(payload)
	})

      .then(r => {
        if (!r.ok) throw new Error(r.status);
        return r.text();
      })
      .then(() => {
        alert('Voti inseriti con successo! :D');
        insertModal.style.display = 'none';
        document.getElementById('appelloSelect').dispatchEvent(new Event('change'));	// retrigger appello so gets the changes
      })
      .catch(err => {
        console.error(err);
        alert('Errore whilst trying inserimento multiplo @lecturer.js');
      });
  });


  
  

  /*------------------------
   |	PUBLISH RESULTS    |
   ------------------------*/
	
   const publishBtn = document.getElementById('publishBtn');
   publishBtn.disabled = true;
   const publishModal = document.getElementById('publishModal');
   const cancelPublish = document.getElementById('cancelPublish');
   const confirmPublish = document.getElementById('confirmPublish');
   const publishBody     = document.getElementById('publishBody');

  
   function updatePublishableResults(){
		const hasAnyToBePub = Array.from(document.querySelectorAll('#tabellaIscritti tr td:nth-child(6)'))	//gettin 6th child which should be 'STATUS'
											.filter(td => td.textContent.trim().toLowerCase() == 'added');	//getting all those with 'added' as status
		
		console.log('To publish count:', hasAnyToBePub.length);
		publishBtn.disabled = !(hasAnyToBePub.length > 0);
		//publishBtn.disabled = false;
   }
 
   // --- 	on press	---
   publishBtn.addEventListener('click', () => {
   		publishBody.innerHTML = ''; // double check empty previous rows
		
     	// for each row, copy matricola, nome, cognome, voto
	 	// order should be	 	0: idStud		1: name		2: surname		3: email	4:voto		5:status
     	document.querySelectorAll('#tabellaIscritti tr').forEach(row => {
		       if (row.children[5].textContent.trim().toLowerCase() == 'added') {
						const tr = document.createElement('tr');
		         		[0,1,2,4].forEach(i => {	// idSt, name, surname, grade
								const td = document.createElement('td');
		           				td.textContent = row.children[i].textContent;
		           				tr.appendChild(td);
		         		});
		        	publishBody.appendChild(tr);
		    	}
		});

   		//show
   		publishModal.style.display = 'flex';
   });

   
   
   //---	cancel btn	 ---
   cancelPublish.addEventListener('click', () => {
	//shown't
     publishModal.style.display = 'none';
   });

   
   
   //---	confirm btn	  ---
   confirmPublish.addEventListener('click', () => {
     const toPublish = Array.from( document.querySelectorAll('#tabellaIscritti tr'))
						     .filter(tr => tr.children[5].textContent.trim().toLowerCase() == 'added')
						     .map(tr => tr.children[0].textContent.trim());

     fetch('PublishResults', {
       		method: 'POST',
       		headers: { 'Content-Type': 'application/json' },
       		body: JSON.stringify({ examId: currentExamId, students: toPublish })
     })
     .then(res => {
       	if (!res.ok) throw new Error(res.status);
       	return res.text();
     })
     .then(() => {
       	alert('Voti pubblicati succesfully :D');
       	publishModal.style.display = 'none';
       	// refresh  iscritti table
       	document.getElementById('appelloSelect').dispatchEvent(new Event('change'));
     })
     .catch(err => {
       	console.error(err);
       	alert('shouldnt be reachable? i hope, @lecturer.js');
     });
   });

   
  
