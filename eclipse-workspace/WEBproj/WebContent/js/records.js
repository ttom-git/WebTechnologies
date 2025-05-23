
/*-------------------
 |	RECORDS TABLE	|
 -------------------*/
document.addEventListener('DOMContentLoaded', () => {
	const tbody = document.querySelector('#recordsTable tbody');

  	fetch('api/records', {credentials: 'include'})
      	.then(res => {
      		if (!res.ok) 
				throw new Error(res.status);
      		return res.json();
    	})
    	.then(records => {
      		records.forEach(r => {
        		const tr = document.createElement('tr');
        		tr.innerHTML = `<td>${r.course}</td>
          						<td>${new Date(r.date).toLocaleString()}</td>
          						<td>${r.id}</td>
          						<td>${new Date(r.examDate).toLocaleDateString()}</td>
						        <td><a href="files/${r.link}" download>${r.link}</a></td>
						        `;
        		tbody.appendChild(tr);
      			});
	  
      	/*		if (typeof makeSortable === 'function')
        			makeSortable(document.getElementById('recordsTable'));
	  			else 
					console.error('maekSortable function not found')
		*/
    	})
    	.catch(err => {
			console.error('errore @records.js', err);
		    const tr = document.createElement('tr');
		    tr.innerHTML = `<td colspan="5" class="error">errore caricamento</td>`;
		    tbody.appendChild(tr);
		});
});
