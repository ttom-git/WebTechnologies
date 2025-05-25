
 /*---------------------
  |   TOAST DISPLAY     |
  ----------------------*/

 function showToast(message) {
   if (!showToast.initialized) {
     if (!document.getElementById('bs-icons')) {
       const link = document.createElement('link');
       link.id = 'bs-icons';
       link.rel = 'stylesheet';
       link.href = 'https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css';
       document.head.appendChild(link);
     }

     const tpl = `
       <div class="toast-container position-fixed bottom-0 end-0 p-3">
         <div id="liveToast"
              class="toast align-items-center border-0"
              role="alert"
              aria-live="assertive"
              aria-atomic="true"
              data-bs-delay="3000">
           <div class="toast-header bg-success text- #5e63a6">
             <i class="bi bi-check-circle-fill me-2"></i>
             <strong class="me-auto">Successo</strong>
             <small class="text-muted ms-2" id="liveToastTime"></small>
             <button type="button"
                     class="btn-close btn-close-white ms-2 mb-1"
                     data-bs-dismiss="toast"
                     aria-label="Close"></button>
           </div>
           <div class="toast-body" id="liveToastBody"></div>
         </div>
       </div>`;
     document.body.insertAdjacentHTML('beforeend', tpl);

     showToast.el    = document.getElementById('liveToast');
     showToast.body  = document.getElementById('liveToastBody');
     showToast.time  = document.getElementById('liveToastTime');
     showToast.toast = bootstrap.Toast.getOrCreateInstance(showToast.el);

     showToast.initialized = true;
   }

   showToast.body.textContent = message;
   showToast.time.textContent = new Date().toLocaleTimeString();
	showToast.toast.show(); 
}



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
	       opt.textContent = appello.date;
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
		
		
		//then updates insertable resutls
		updateInsertableResults();
		
		//then updates publishable results
		updatePublishableResults();
		
		//then updates verbalizable resutls
		updateVerbalizableResults();
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

     // if sorting by grade=> use gradeOrder index
     if (key == "grade") {
       v1 = gradeOrder.indexOf(v1);
       v2 = gradeOrder.indexOf(v2);
     }

     // try numeric compare first
     const n1 = parseFloat(v1), n2 = parseFloat(v2);
     if (!isNaN(n1) && !isNaN(n2)) {
       return asc ? n1 - n2 : n2 - n1;
     }
     // otherwise string compare
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
  //insertModal.style.display = 'none';			//hiding mode initally
  insertModal.classList.add('hidden');
  
  

  const insertBtn = document.getElementById('insertBtn');
  insertBtn.disabled = true;	//should keep button disabled until rows are loaded 
  insertBtn.classList.add('hidden');

  // --------	apppelloSelect handler :	-------------
    function updateInsertableResults(){
		const appelloId = document.getElementById('appelloSelect').value;
	currentExamId = appelloId; 	// storing appelloId in session for InserResultServlet 

	 fetch('StoreExamId', {
	   method: 'POST',
	   headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
	   body: `examId=${encodeURIComponent(appelloId)}`
	 });
	 
	 const pendingRows = Array.from(document.querySelectorAll('#tabellaIscritti tr td:nth-child(6)'))	//gettin 6th child which should be 'STATUS'
	 									.filter(td => (td.textContent.trim().toLowerCase() == 'pending' || td.textContent.trim().toLowerCase() == 'added'));	//getting all those with 'added' as status


	 console.log('To insert count:', pendingRows.length);
	 if (pendingRows.length > 0) {
	   insertBtn.classList.remove('hidden');
	   insertBtn.disabled = false;
	 } else {
	   insertBtn.classList.add('hidden');
	   insertBtn.disabled = true;
	 }
  }


  insertBtn.addEventListener('click', () => {
    const rows = Array.from(document.querySelectorAll('#tabellaIscritti tr'));
    const body = document.getElementById('tableBody');
    body.innerHTML = '';

    rows.forEach(tr => {
      const status = tr.children[5].textContent.trim().toLowerCase();
      if (status == 'pending' || status == 'added') { 	// also 'added' cosi che si possa modificare quelli non ancora pubblicati :)  
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
        `;	// :)

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
    //insertModal.style.display = 'flex';
	insertModal.classList.remove('hidden');
  });
  //cancel button  
  document.getElementById('cancel').onclick = () => {
    //insertModal.style.display = 'none';
	insertModal.classList.add('hidden');
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
        grade: sel.value
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
        //alert('Voti inseriti con successo! :D');
		showToast('Voti inseriti con successo! :D');

        //insertModal.style.display = 'none';
		insertModal.classList.add('hidden');
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
   publishBtn.classList.add("hidden");

   const publishModal = document.getElementById('publishModal');
   const cancelPublish = document.getElementById('cancelPublish');
   const confirmPublish = document.getElementById('confirmPublish');
   const publishBody = document.getElementById('publishBody');

  
   function updatePublishableResults(){
		const hasAnyToBePub = Array.from(document.querySelectorAll('#tabellaIscritti tr td:nth-child(6)'))	//gettin 6th child which should be 'STATUS'
											.filter(td => td.textContent.trim().toLowerCase() == 'added');	//getting all those with 'added' as status
		
		console.log('To publish count:', hasAnyToBePub.length);
		if(hasAnyToBePub.length > 0){
			publishBtn.disabled = false;
			publishBtn.classList.remove("hidden");
		} else {
			publishBtn.disabled = true;
			publishBtn.classList.add("hidden");
		}
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
   		//publishModal.style.display = 'flex';
		publishModal.classList.remove('hidden');
   });

   
   
   //---	cancel btn	 ---
   		cancelPublish.addEventListener('click', () => {
		//shown't
     	//publishModal.style.display = 'none';
	 	publishModal.classList.add('hidden');

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
       	if (!res.ok) 
			throw new Error(res.status);
       	return res.text();
     })
     .then(() => {
       	//alert('Voti pubblicati succesfully :D');
		showToast('Voti pubblicati succesfully :D');

       	//publishModal.style.display = 'none';
		publishModal.classList.add('hidden');

       	// refresh  iscritti table
       	document.getElementById('appelloSelect').dispatchEvent(new Event('change'));
     })
     .catch(err => {
       	console.error(err);
       	alert('shouldnt be reachable? i hope, @lecturer.js');
     });
   });

   
   


   /*------------------------
    |	VERBALIZE RESULTS   |
    ------------------------*/
   const verbalizeBtn = document.getElementById('verbalizeBtn');
   verbalizeBtn.disabled = true;
   verbalizeBtn.classList.add('hidden');

   /** 
    * show or hide + enable the Verbalizza button
    * whenever the table is refreshed.
    */
   function updateVerbalizableResults() {
     const anyPublished = Array.from(
       document.querySelectorAll('#tabellaIscritti tr td:nth-child(6)')
     ).some(td => td.textContent.trim().toLowerCase() === 'published');

     if (anyPublished) {
       verbalizeBtn.classList.remove('hidden');
       verbalizeBtn.disabled = false;
     } else {
       verbalizeBtn.classList.add('hidden');
       verbalizeBtn.disabled = true;
     }
   }

   
   // when click on "verbalizza""
   verbalizeBtn.addEventListener('click', () => {
     const toVerbalize = Array.from(
       document.querySelectorAll('#tabellaIscritti tr')
     )
       .filter(tr =>
         tr.children[5].textContent.trim().toLowerCase() === 'published'
       )
       .map(tr => tr.children[0].textContent.trim());

     fetch('verbalizeResults', {
       method: 'POST',
       headers: { 'Content-Type': 'application/json' },
       body: JSON.stringify({ examId: currentExamId, students: toVerbalize })
     })
       .then(res => {
         if (!res.ok) throw new Error(res.status);
         return res.json();
       })
       .then(data => {
         // Navigate the browser to the new verbale page
         window.location.href = data.url;
       })
       .catch(err => {        
		 console.error(err);
		 if (err.error === 'DUPLICATE_ENTRY') {
		   alert('Duplicate entry');
		 } else {
		   alert('Errore durante la verbalizzazion');
       	 }
	   })
   });

   
  
