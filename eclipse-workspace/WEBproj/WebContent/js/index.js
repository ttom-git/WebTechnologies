const loginForm    = document.getElementById("loginForm");
const errorMessage = document.getElementById("errorMessage");
//const pwdModal     = document.getElementById("pwdModal");
const pwdForm      = document.getElementById("pwdForm");
const pwdEmail     = document.getElementById("pwdEmail");
const pwdTipo      = document.getElementById("pwdTipo");

const pwdModal = new bootstrap.Modal(document.getElementById("pwdModal"));

let pendingEmail, pendingTipo;

function formDataToUrlEncoded(formData) {
	const params = new URLSearchParams();
  		for (const [k, v] of formData.entries()) {
    		params.append(k, v);
  		}
  	return params;
}
	
	/*-----------
	|	LOGIN    |
	-------------*/
loginForm.addEventListener("submit", async e => {
  	e.preventDefault();
  	errorMessage.style.display = "none";

  	const fd = new FormData(loginForm);
  	pendingEmail = fd.get("email");
  	pendingTipo  = fd.get("tipo");

  	try {
	    const res = await fetch("login", {
    	  	method: "POST",
      		headers: { "Content-Type": "application/x-www-form-urlencoded" },
      		body: formDataToUrlEncoded(fd)
    	});
	
	    if (res.redirected) {
	      	return window.location.href = res.url;
	    }
	
	    const data = await res.json();
	    if (data.forcePwdChange) {
	      	// campi hidden
	      	pwdEmail.value = pendingEmail;
	      	pwdTipo.value  = pendingTipo;
			
	      	//pwdModal.classList.remove("hidden");
		  	pwdModal.show();
	    }
	    else if (data.success && data.redirect) {
	      	window.location.href = data.redirect;
	    }
	    else if (data.error) {
	      	errorMessage.textContent = data.error;
	      	errorMessage.classList.add("alert", "alert-danger");
	      	errorMessage.style.display = "block";
	    }
  	} catch (err) {
    	console.error("Errore durante il login:", err);
    	errorMessage.textContent = "Errore di rete o server.";
    	errorMessage.classList.add("alert", "alert-danger");
    	errorMessage.style.display = "block";
  	}
});

	
	/*----------------------
	|	CAMBIO PASSWORD    |
	-----------------------*/
document.getElementById("pwdSaveBtn").addEventListener("click", async () => {
	//pwdForm.addEventListener("submit", async e => {
	//e.preventDefault();
  	const fd = new FormData(pwdForm);

  	if (fd.get("newPassword") !== fd.get("newPasswordConfirm")) {
    	return alert("Le password non coincidono");
  	}

  	try {
    	const res = await fetch("changePassword", {
      		method: "POST",
      		headers: { "Content-Type": "application/x-www-form-urlencoded" },
      		body: new URLSearchParams(fd)
    	});

		if (res.ok) {
      		//pwdModal.classList.add("hidden");
	  		pwdModal.hide();

      		window.location.href = "index.html";
    	} else {
      		const text = await res.text();
      		alert(text || "Errore durante il cambio password");
    	}
  	} catch (err) {
    	console.error("Errore cambio password:", err);
    	alert("Errore di rete o server durante il cambio password");
  	}
});
