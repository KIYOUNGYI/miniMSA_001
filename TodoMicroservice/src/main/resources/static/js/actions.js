$(document).ready(function(){
       $("#signindiv").show();         /* it shows the sign in and hide the operation sections */
       $("#operationdiv").hide();
     });
    /* function used to get the jwt from cookies after having saved it, for later async ajax calls to microservices */
    function getCookie(name) {
       var value = "; " + document.cookie;
       var parts = value.split("; " + name + "=");
       if (parts.length == 2)
          return parts.pop().split(";").shift();
    }
    /* function used to change the text of the url giving the impression we're changing page */
    function changeUrl(title, url) {
       if (typeof (history.pushState) != "undefined") {
          var obj = { Title: title, Url: url };
          history.pushState(obj, obj.Title, obj.Url);
       } else {
          alert("Browser does not support HTML5.");
       }
    }
    function showToDos() {
       //ajax call to ToDoMicroservice/showToDos
       $.ajax({                                                                        /* Ajax call to AccountMicroservice */
           url: 'http://localhost:8383/showToDos',
           type: "POST",
           success: function (data, status, xhr) {
              console.log(data);
              $("#bodyTableToDos").empty();
              jQuery.each(data.response, function(i, val) {
                 $("#bodyTableToDos").append("<tr><th>" + val.id + "</th><td>" + val.description + "</td><td>" + val.priority + "</td><td>" + convertDate(val.date) + "</td><td><p class='delete' onclick='deleteToDo(" + val.id + ")'></p></td></tr>");
              });
           },
           error: function(result) {
              alert("Error!");
              console.log(result);
           }
       });
       //ajax call to StatisticsMicroservice/getStatistics
       $.ajax({                                                                        /* Ajax call to AccountMicroservice */
           url: 'http://localhost:8384/getStatistics',
           type: "POST",
           data: {
             jwt: getCookie("jwt"),
             email: $("#email").val()
           },
           success: function (data, status, xhr) {
              console.log(data);
              $("#bodyTableStatistics").empty();
              jQuery.each(data.response, function(i, val) {
                 $("#bodyTableStatistics").append("<tr><th>" + val.id + "</th><td>" + val.description + "</td><td>" + convertDate(val.date) + "</td><td>" + val.email + "</td></tr>");
              });
           },
           error: function(result) {
              alert("Error!");
              console.log(result);
           }
       });
    }
    function deleteToDo(id) {
       //ajax call to ToDoMicroservice/deleteToDo
       $.ajax({
          url: 'http://localhost:8383/deleteToDo/' + id,
          type: "POST",
          success: function (data, status, xhr) {
             console.log(data);
             //call to showToDos() javascript function to update the list
             showToDos();
          },
          error: function(result) {
             alert("Error!");
             console.log(result);
          }
       });
    }
    function convertDate(inputFormat) {
        function pad(s) { return (s < 10) ? '0' + s : s; }
        var d = new Date(inputFormat);
        return [pad(d.getDate()), pad(d.getMonth()+1), d.getFullYear()].join('/');
    }
    /* sign in submit function */
       $("#submit").click(function(e) {
          e.preventDefault();
          $.ajax({                                                        /* Ajax call to AccountMicroservice for login */
             url: 'http://localhost:8382/login',
             type: "POST",
             data: {
                email: $("#email").val(),
                password: $("#password").val()
             },
             success: function (data, status, xhr) {
//                console.log(getResponseHeader);
//                console.log(response)
                console.log(data);
                console.log(data.jwt);
//                var obj = JSON.parse(data);
//                alert(obj.jwt);
//                console.log(status);
//                console.log(xhr);
//                console.log(xhr.getResponseHeader("jwt"));
                $("#signindiv").hide();                              /* it hides sign in section and shows operation section */
                $("#operationdiv").show();
                changeUrl("ToDos","/todos.html");               /* it changes url after sign in */
//                document.cookie = "jwt=" + xhr.getResponseHeader("jwt");  /* it saves in cookies the received token */
                document.cookie = "jwt="+data.jwt;
                $("#newToDoEmail").val($("#email").val());
                $("#password").val("");
                showToDos();
             },
             error: function(result) {
                alert("Sign in failed!");
                console.log(result);
             }
          });
       });
    /* sign out submit function */
    $("#signout").click(function(e) {
       e.preventDefault();
       document.cookie = "jwt=;expires=Thu, 01 Jan 1970 00:00:01 GMT;";        /* it cancels jwt from cookies */
       $("#signindiv").show();                                            /* it hides operations and shows sign in section */
       $("#operationdiv").hide();
       $("#email").val("");
       emptyForm();
       $("#newToDoEmail").val("");
       alert("You're logged out!");
       changeUrl("Signin","/signin.html");                                     /* it changes url again to signin.html */
    });
    $("#newToDo").click(function(e) {
       e.preventDefault();
       if( $("#newToDoPriority").val() == null ){
           alert("Insert a valid priority value");
       } else if ($("#newToDoDescription").val() == ""){
           alert("Insert a valid description value");
       }else{
           //ajax call to ToDoMicroservice/newToDo
           $.ajax({                                                        /* Ajax call to AccountMicroservice for login */
              url: 'http://localhost:8383/newToDo',
              type: "POST",
              data: {
                 description: $("#newToDoDescription").val(),
                 priority: $("#newToDoPriority").val(),
                 fkUser: $("#newToDoEmail").val()
              },
              success: function (data, status, xhr) {
                 emptyForm();
                 showToDos();
              },
              error: function(result) {
                 alert("Error!");
                 console.log(result);
              }
           });
       }
    });
    function emptyForm() {
       $("#newToDoDescription").val("");
       $("#newToDoPriority").val('');
    }