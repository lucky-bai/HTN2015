document.addEventListener('DOMContentLoaded', function() {
  $('#username').val(localStorage.getItem("username"));
  $('#full_name').val(localStorage.getItem("full_name"));
  chrome.idle.onStateChanged.addListener(function(state) {
    console.log(state);
  });

  var checkPageButton = document.getElementById('sendData');
  checkPageButton.addEventListener('click', function() {
    chrome.tabs.getSelected(null, function(tab) {
      var lastSent = parseInt(localStorage.getItem("lastSent")) ? parseInt(localStorage.getItem("lastSent")) : 0;
      var full_name = $('#full_name').val()
      var username = $('#username').val()

      localStorage.setItem("full_name", full_name);
      localStorage.setItem("username", username);
      chrome.history.search({"text":"", "maxResults":2147483647, "startTime": lastSent}, function(history) {
        _.forEach(history, function(history) {
          // Delete uneeded keys
          delete history["id"];
          delete history["title"];
          delete history["typedCount"];
          delete history["visitCount"];

          // Rename keys
          history["timestamp"] = Math.floor(history["lastVisitTime"]/1000);
          delete history["lastVisitTime"];
          history["subject"] = history["url"];
          delete history["url"];

          // Add extra keys
          history["source"] = "chrome";
          history["event_type"] = "page_view";
        });
        console.log(history);
        // Check if User Exists
        $.get("http://www.sleepguardian.co/user/" + username)
          .done(function (data) {
            // User exists
            console.log("User exists");
            send_history(username, history);
          }).fail(function (data) {
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
              send_history(username, history);
            }).fail(function (data) {
              console.log("Failed to create user");
            });
          });
      });
    });
  }, false);
}, false);

function send_history(username, history)  {
  if(!_.isEmpty(history)) {
    $.ajax({
      method: "POST",
      url: "http://www.sleepguardian.co/user/" + username + "/timestamps",
      contentType: "application/json",
      data: JSON.stringify(history)
    }).done(function (data) {
      localStorage.setItem("lastSent", Date.now());
    }).fail(function (data) {
      console.log("Data Saving Failed: " + data);
    });
  } else {
    console.log("No Data to Send");
  }
}