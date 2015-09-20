document.addEventListener('DOMContentLoaded', function() {
  // IDLE
  chrome.idle.onStateChanged.addListener(function (state) {
    console.log(state);
  });

  chrome.alarms.onAlarm.addListener(function (alarm) {
    if (alarm["name"] == "sendDataAlarm") {
      send_data();
    }
  });

  if (!_.isEmpty(localStorage.getItem("username"))) {
    $("#form__container").hide();
    $("#name").text(localStorage.getItem("full_name"));
  } else {
    $("#welcome__container").hide();
  }

  $('#loginForm').submit(function (evt) {
    // Login
    chrome.tabs.getSelected(null, function(tab) {
      var full_name = $('#full_name').val()
      var username = $('#username').val()
      
      $("#sendDataButton").val("Logging In...");
      $.get("http://www.sleepguardian.co/user/" + username)
        .done(function (data) {
          // User exists
          console.log("User exists");
          $("#form__container").hide();
          $("#welcome__container").show();
          $("#name").text(data["full_name"]);
          localStorage.setItem("username", data["username"]);
          localStorage.setItem("full_name", data["full_name"]);
          chrome.alarms.create("sendDataAlarm", {"when": Date.now(), "periodInMinutes": 1440});
        })
        .fail(function (data) {
          // User does not exist
          console.log("User does not exist");
          $.ajax({
            method: "POST",
            url: "http://www.sleepguardian.co/user",
            contentType: "application/json",
            data: JSON.stringify({ "username": username,
                    "full_name": full_name })
          }).done(function (data) {
            console.log("Created new User");
            $("#form__container").hide();
            $("#welcome__container").show();
            $("#name").text(data["full_name"]);
            localStorage.setItem("username", data["username"]);
            localStorage.setItem("full_name", data["full_name"]);
            chrome.alarms.create("sendDataAlarm", {"when": Date.now(), "periodInMinutes": 1440});
          }).fail(function (data) {
            console.log("Failed to create user");
            $("#sendDataButton").val("Failed to Login. Try again.");
          });
        });
    });
    evt.preventDefault();
  });

  $('#sendDataForm').submit(function (evt) {
    send_data();
    evt.preventDefault();
  });
}, false);

function send_data() {
    // Send Data
    var lastSent = parseInt(localStorage.getItem("lastSent")) ? parseInt(localStorage.getItem("lastSent")) : 0;
    
    chrome.history.search({"text":"", "maxResults":2147483647, "startTime": lastSent}, function(history) {
      _.forEach(history, function(history) {
        // Delete uneeded keys
        delete history["id"];
        delete history["title"];
        delete history["typedCount"];
        delete history["visitCount"];
        delete history["url"];

        // Rename keys
        history["timestamp"] = Math.floor(history["lastVisitTime"]/1000);
        delete history["lastVisitTime"];

        // Add extra keys
        history["source"] = "chrome";
        history["event_type"] = "page_view";
      });
      
      // Send Data
      $("#sendDataButton").val("Sending...");
      if(!_.isEmpty(history)) {
        $.ajax({
          method: "POST",
          url: "http://www.sleepguardian.co/user/" + localStorage.getItem("username") + "/timestamps",
          contentType: "application/json",
          data: JSON.stringify(history)
        }).done(function (data) {
          localStorage.setItem("lastSent", Date.now());
          console.log("Data Sent.");
          $("#sendDataButton").val("Data Sent! Send Again.");
        }).fail(function (data) {
          console.log("Data Saving Failed: " + data);
          $("#sendDataButton").val("Send Data Failed. Send Again.");
        });
      } else {
        console.log("No Data to Send");
        $("#sendDataButton").val("No Data To Send. Send Again.");
      }
    });
}