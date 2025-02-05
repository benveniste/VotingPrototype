    function adder(id, maxVotes) {
      let lizst = document.getElementById("choices" + id)
      let element = document.getElementById("myInput" + id)
      let entry = element.value.trim();
      if (entry === "") {
        return;
      } else if (lizst.childNodes.length == maxVotes) {
        alert("No more choices allowed.");
        return;
      }

      for (let i = 0; i < lizst.childNodes.length; i++) {
        if (entry.toLowerCase() === lizst.childNodes[i].innerHTML.toLowerCase()) {
          return;
        }
      }
      let li = document.createElement("li");
      li.appendChild(document.createTextNode(entry));
      lizst.appendChild(li);
      element.value = "";
    }

    async function submit(ballot, okayUrl) {
      const settings = {
        method: 'POST',
        body: JSON.stringify(Object.fromEntries(ballot)),
        headers: {
          'Content-Type': 'application/json',
        }
      }
      const response = await fetch("submitBallot", settings);
      const text = await response.text();
      if (text.startsWith('OK|')) {
        window.location.href = okayUrl + '?ballotContract=' + text.substring(3);
      } else {
        alert(text);
      }
    }
