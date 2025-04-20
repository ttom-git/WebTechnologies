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

