// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

var tabId = parseInt(window.location.search.substring(1));
var startTime = Math.round(+new Date()/1000);
var list = "";
function myfunc(){
 var name = startTime+'log.txt';
 window.requestFileSystem = window.requestFileSystem || window.webkitRequestFileSystem;
 window.requestFileSystem(window.TEMPORARY, 1024*1024, function(fs) {
    fs.root.getFile(name, {create: true}, function(fileEntry) { // test.bin is filename
        fileEntry.createWriter(function(fileWriter) {
           

            fileWriter.addEventListener("writeend", function() {
                // navigate to file, will download
                location.href = fileEntry.toURL();
            }, false);

              fileWriter.onerror = function(e) {
                console.log('Write failed: ' + e.toString());
              };

            // Create a new Blob and write it to log.txt.
            var blob = new Blob([list], {type: 'text/plain'});
            fileWriter.write(blob);
        }, function() {});
    }, function() {});
}, function() {});
}


window.addEventListener("load", function() {
  chrome.debugger.sendCommand({tabId:tabId}, "Network.enable");
  chrome.debugger.onEvent.addListener(onEvent);
  var x = document.getElementById("cbutton");
  x.addEventListener("click", clearFunction);
  var y = document.getElementById("sbutton");
  y.addEventListener("click", myfunc);
});

window.addEventListener("unload", function() {
  chrome.debugger.detach({tabId:tabId});
});

var requests = {};

function parserURL(url) {
    var parser = document.createElement('a'),
        searchObject = {},
        queries, split, i;
    // Let the browser do the work
    parser.href = url;
    // Convert query string to object
    queries = parser.search.replace(/^\?/, '').split('&');
    for( i = 0; i < queries.length; i++ ) {
        split = queries[i].split('=');
        searchObject[split[0]] = split[1];
    }
    return {
        protocol: parser.protocol,
        host: parser.host,
        hostname: parser.hostname,
        port: parser.port,
        pathname: parser.pathname,
        search: parser.search,
        searchObject: searchObject,
        hash: parser.hash
    };
}

function onEvent(debuggeeId, message, params) {
  if (tabId != debuggeeId.tabId)
    return;

  if (message == "Network.requestWillBeSent") {
    var requestDiv = requests[params.requestId];
    if (!requestDiv) {
      var req = {};
      req = parserURL(params.request.url); 
      keyword = getParameterByName('q',req.search);
      if(keyword){
        var unix = Math.round(+new Date()/1000);
        CreateRow(keyword, unix);
      }
    }
  }
}



function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, "\\$&");
    var regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function CreateRow(keyword, time) {
    list += time+  ";" + keyword + "\n";
    var table = document.getElementById("TABLE");
    var row = table.insertRow(1);
    var cell = row.insertCell(0);
    var cell_two = row.insertCell(1);
    cell.innerHTML = keyword;
    cell_two.innerHTML = time-startTime;
}

function clearFunction(){
 var myTable = document.getElementById("TABLE");
 var rowCount = myTable.rows.length;
 for (var x=rowCount-1; x>0; x--) {
    myTable.deleteRow(x);
 }
}
