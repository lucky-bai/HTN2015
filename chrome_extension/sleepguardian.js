document.addEventListener('DOMContentLoaded', function() {
  chrome.idle.onStateChanged.addListener(function(state) {
    console.log(state);
  });

  var checkPageButton = document.getElementById('checkPage');
  checkPageButton.addEventListener('click', function() {
    chrome.tabs.getSelected(null, function(tab) {
      chrome.history.search({"text":"", "maxResults":2147483647, "startTime": 0}, function(history) {
        console.log(history);
      });
    });
  }, false);
}, false);