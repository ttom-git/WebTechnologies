	
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
        document.getElementById("studentInfo").textContent =
          `${data.name} ${data.surname} | Codice Persona: ${data.codePersona}`;
      } catch (e) {
        console.error("json esploso:", e.getMessage());
      }
    })
    .catch(e => console.error("Generic error @student,js:", e));
});



	/*---------------------
	 |	 EXAMS DISPLAY	  |
	 ----------------------*/
	 
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

	 
	 
	 
	 
	/*---------------------
	 |	 REFUSE BUTTON	  |
	 ----------------------*/

//https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_style_display4

function showRefusalConfirm(exam) {
  const popup = document.getElementById('confirmPopup');
  popup.style.display = 'block';	//shouldnt this overlay prev style?	https://www.w3schools.com/jsref/tryit.asp?filename=tryjsref_style_display

  document.getElementById('cancelRefuse').onclick = () => {
    popup.style.display = 'none';
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


