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

    async function submit(ballot) {
      let response = await fetch("submitBallot", {
        method: "POST",
        body: JSON.stringify(Object.fromEntries(ballot)),
        headers: {
          "Content-type": "application/json; charset=UTF-8"
        }
      });
      let result = await response.text();
      return result;
    }
