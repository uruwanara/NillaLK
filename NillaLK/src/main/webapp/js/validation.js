function validatePestForm() {
    var x = document.forms["addPestForm"]["pestName"].value;
    if (x == "") {
      alert("Pest Name must be filled out");
      return false;
    }    
    var y = document.forms["addPestForm"]["pestSciName"].value;
    if (y == "") {
      alert("Pest Sciencetific Name must be filled out");
      return false;
    }
    var z = document.forms["addPestForm"]["pestDesc"].value;
    if (z == "") {
      alert("Pest Description must be filled out");
      return false;
    }    
    var a = document.forms["addPestForm"]["pesticide"].value;
    if (a == "") {
      alert("Pesticide must be filled out");
      return false;
    }
  }

  