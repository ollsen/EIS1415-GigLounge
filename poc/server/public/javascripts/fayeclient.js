$(document).ready(function() {
    var $chat = $('#chat');
          $('#fire').on('click',null, function() {
            var url = 'http://localhost:3000/pubsub/message';

            var message = {message: user+': ' + $chat.val()};
            var dataType = 'json';
            $.ajax({
              type: 'POST',
              url: url,
              data: message,
              dataType: dataType,
            });
            $chat.val('');
          });
          var client = new Faye.Client('/faye',{
            timeout: 20
          });

          client.subscribe('/channel', function(message) {
            $('#messages').append('<p>' + message.text + '</p>');
    });
});