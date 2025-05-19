	
	/*---------------------
	 |	 INFO'S DISPLAY	  |
	 ----------------------*/
 
/*document.addEventListener('DOMContentLoaded', () => {
  fetch("getUserInfo")
    .then(res => res.json())
    .then(data => {
      console.log("Raw fetch:", data);
      document.getElementById("studentInfo").textContent =
        `${data.name} ${data.surname} | Codice Persona: ${data.codePersona}`;
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
	        		document.getElementById("studentInfo").textContent = `${data.name} ${data.surname} | Codice Persona: ${data.codePersona}`;
	      		} catch (e) {
	        		console.error("json esploso:", e.getMessage());
	      		}
	    	})
	    	.catch(e => console.error("Generic error @student,js:", e));

	
	/*---------------------
	 |	 COURSES FETCH	  |				    recupera i corsi per <select id="courseSelect">
	 ----------------------*/
		 fetch('api/courses')
		 	.then(res => {
		    	if (!res.ok) throw new Error('Errore fetch corsi');
		       	return res.json();
		    })
		    .then(courses => {
		    	console.log('Corsi caricati:', courses);
		       	const courseSelect = document.getElementById('courseSelect');
		       	courseSelect.innerHTML = '';       // clean della select se no tiene i vecchi :)
	
		       	// opzione iniziale
		       	const placeholder = document.createElement('option');
		       	placeholder.textContent = '- Seleziona corso -';
		       	placeholder.disabled = true;
		       	placeholder.selected = true;
		       	courseSelect.appendChild(placeholder);
	
		       	// opzioni vere
		       	courses.forEach(c => {
		         	const opt = document.createElement('option');
		         	opt.value = c.idCourse;    
		         	opt.textContent = c.name;
		         	courseSelect.appendChild(opt);
		       	});
	
		      	 // listener per cambio corso
			   	courseSelect.addEventListener('change', e => {
			     	loadExamDates(e.target.value);
			   	});
	
		       	// se c’è almeno un corso, carica subito le date del primo per richiesta mi pare delle specz
		       	if (courses.length) {
		         	courseSelect.value = courses[0].idCourse;
		         	loadExamDates(courses[0].idCourse);
		       	}
			})
		    .catch(err => {
		    	console.error(err);
		       	document.getElementById('examList').innerHTML = '<li>Errore caricamento dei corsi</li>';
	     	});
			
			
	});
		 
		  
		 
  	/*---------------------
   	|	 EXAMS DATES	  |					 Carica date appello del corso
	----------------------*/

	function loadExamDates(idCourse) {
	  	console.log('Chiamo loadExamDates con idCourse =', idCourse);

	  	const dateSelect = document.getElementById('dateSelect');
	  	dateSelect.disabled = true;          // disabilito finché carico
	  	dateSelect.innerHTML = '';      

	  	fetch(`api/exams?corsoId=${encodeURIComponent(idCourse)}`)
	    	.then(res => res.json())
	    	.then(dates => {
	    		if (dates.length <= 0) {
	        		dateSelect.innerHTML = '<option>Nessun appello</option>';
	        		return;
	      		}

	    dateSelect.disabled = false;	// abilito bottone
	    dateSelect.innerHTML = dates
	    	.map(d => `<option value="${d.id}">${d.date}</option>`)
	        .join('');

	    loadResult(dates[0].id);	     // seleziono il primo in automatico cosi sei felice fede :)
	    })
	    .catch(err => {
	      	console.error(err);
	      	dateSelect.innerHTML = '<option>Errore nel caricamento</option>';
	    });
	}

	
	
	document.getElementById('dateSelect').addEventListener('change', e => loadResult(e.target.value));


	
	
	/*---------------------
	 |	 EXAMS DISPLAY	  |				 Carica e mostra l’esito per l’idExam selezionato
	 ----------------------*/
	

	function loadResult(idExam) {
		console.log('→ loadResult() chiamato con idExam =', idExam);
	  	fetch(`api/results?idExam=${idExam}`)
	    	.then(res => res.json())
	    	.then(r => {
	      		console.log('  JSON esito ricevuto:', r);
	      		const ul = document.getElementById('examList');
	      		ul.innerHTML = '';
				
				const li = document.createElement('li');
				//se verbalized o rejected showo, se published trash displayed, 'Voto non ancora definito' altrimenti
					switch (r.status){
						case 'verbalized': 							
							li.textContent = `${r.courseName} – ${r.date} – Voto: ${r.grade}`;
							// da fare tipo verde o verbalizzato
							li.classList.add('verbalized');
							ul.appendChild(li);
							
							trash.style.display = 'none';
							break;
							
						case 'published':
							li.textContent = `${r.courseName} – ${r.date} – Voto: ${r.grade}`;
			
							li.draggable = true;
						  	li.dataset.idExam = idExam;
		
						  	li.addEventListener('dragstart', e => {
						    	e.dataTransfer.setData('text/plain', idExam);
						    	e.target.style.opacity = '0.6';
						  	});
						  	li.addEventListener('dragend', e => {
						    	e.target.style.opacity = '';	//dovrebbe ripristinare stile base
						  	})
						  	ul.appendChild(li);
						  
						  	trash.style.display = 'block'; // display trash area
						  	break;
		
							
						case 'rejected':
							li.textContent = `${r.courseName} – ${r.date} – Voto: ${r.grade}`;
							//da fare tipo rosso o scritta rejected 
							li.classList.add('rejected');

							ul.appendChild(li);
							
							trash.style.display = 'none';
							break;
							
							
						default :
							ul.innerHTML = '<li>Voto non ancora definito</li>';
							trash.style.display = 'none';
							break;
					}

	    	})
	    	.catch(err => {
	      		console.error(err);
	      		document.getElementById('examList')
	              	.innerHTML = '<li>Errore duratne caricamento dell’esito</li>';
	    	});
		
	}

	
	
	/*---------------------
	 |	 TRASH DISPLAY	  |
	 ----------------------*/

	document.addEventListener('DOMContentLoaded', () => {
		const trash = document.getElementById('trash');
		
		
		// trascino sopra
		trash.addEventListener('dragover', e => {
		  	e.preventDefault();           // permette il drop
		  	trash.classList.add('over');  // cambia sfondo con .over { background: [qualcosa :)] }
			//trash.classList.add('animate__animated', 'animate__shake');	//shakin animaz 
		});
	
		// esco dalla zona
		trash.addEventListener('dragleave', () => {
		  	trash.classList.remove('over');
			trash.classList.remove('animate__shake');
		});
	
		// droppo sopra
		trash.addEventListener('drop', e => {
		  	e.preventDefault();
			console.log('>>> exam trashed!', e.dataTransfer.getData('text/plain'));
		  	trash.classList.remove('over');
	
		  	const idExam = e.dataTransfer.getData('text/plain');
		  	if (!idExam) 
				return;
	
			// mostro il popup conferma/cancel
		  	const popup = document.getElementById('confirmPopup');
		  	//popup.style.display = 'block';
			popup.classList.remove('hidden');
			window._toReject = idExam;
		});
	
		document.getElementById('cancelRefuse').onclick = () => {
		 	 //document.getElementById('confirmPopup').style.display = 'none';
			 document.getElementById('confirmPopup').classList.add('hidden');
		  	window._toReject = null;
		};
	
		document.getElementById('confirmRefuse').onclick = () => {
			const idExam = window._toReject;
		  	if (!idExam) 
				return;
	
		 	 fetch('api/RejectGradeServlet', {
		    	method: 'POST',
		    	headers: {'Content-Type':'application/x-www-form-urlencoded'},
		   		body: 'idExam=' + encodeURIComponent(idExam)
		  	})
		  	.then(res => {
		    	if (res.ok) {
		      		alert('Voto rifiutato correttamente');
		      		location.reload();
		    	} else {
		      	alert('Errore nel rifiuto del voto');
				}
		  	});
		};
	});
	
	/*---------------------
	 |	 EXAMS DISPLAY	  |
	 ----------------------*/
	 
	 /*
	 document.addEventListener('DOMContentLoaded', () => {
	   fetch('StudentDataServlet')
	     .then(res => res.json())
	     .then(data => {
	       const ul = document.getElementById('examList');
	       data.forEach(exam => {
	         const li = document.createElement('li');
	         li.textContent = `${exam.courseName} (${exam.date}) – Voto: ${exam.grade} [${exam.status}]`;
	         
	         if (exam.status !== 'verbalized') {
	           li.setAttribute('draggable', 'true');
	           li.dataset.idExam = exam.idExam;

	           li.addEventListener('dragstart', e => {
	             e.dataTransfer.setData('text/plain', li.dataset.idExam);
	           });
	         }

	         ul.appendChild(li);
	       });
	     });

	   const trash = document.getElementById('trash');

	   trash.addEventListener('dragover', e => {
	     e.preventDefault();
	     trash.style.background = "#ffe0e0";
	   });

	   trash.addEventListener('dragleave', () => {
	     trash.style.background = "";
	   });

	   trash.addEventListener('drop', e => {
	     e.preventDefault();
	     trash.style.background = "";

	     const idExam = e.dataTransfer.getData('text/plain');

	     if (!idExam) return;

	     if (confirm("Vuoi davvero rifiutare questo voto?")) {
	       fetch('RejectGradeServlet', {
	         method: 'POST',
	         headers: {'Content-Type': 'application/x-www-form-urlencoded'},
	         body: 'idExam=' + encodeURIComponent(idExam)
	       })
	       .then(res => {
	         if (res.ok) {
	           alert("Voto rifiutato.");
	           location.reload();
	         } else {
	           alert("Errore nel rifiuto del voto.");
	         }
	       });
	     }
	   });
	 });

	 
	 */
	 
	 
	/*---------------------
	 |	 REFUSE BUTTON	  |
	 ----------------------*/

//https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_style_display4

function showRefusalConfirm(exam) {
  	const popup = document.getElementById('confirmPopup');
  	//popup.style.display = 'block';	//shouldnt this overlay prev style?	https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_style_display
	popup.classList.remove('hidden');
	
  	document.getElementById('cancelRefuse').onclick = () => {
    	//popup.style.display = 'none';
		document.getElementById('confirmPopup').classList.add('hidden');

  	};

  	document.getElementById('confirmRefuse').onclick = () => {
    	fetch('api/refuseGrade', {
      		method: 'POST',
      		headers: {'Content-Type': 'application/json'},
      		body: JSON.stringify({
        		examId: exam.idExam,
        		studentId: currentStudentId
      		})
    	}).then(res => {
      		if (res.ok) {
        		alert("Voto rifiutato con successo.");
        		location.reload(); // refresh exams list
      		} else {
        		alert("D'oh. Something went wrong");
      		}
    	});
  	};
}


