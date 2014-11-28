var mongoose = require('mongoose');
var request = require('request');
var db = require('./db');
var User = db.model('User');
exports.getuser = function(username, req_id) {
    User.findOne({username : req.params.username}, function(err, user) {
        db_user.email = req.body.email;
        return db_user.save(function (err) {
            if (!err) {
                console.log("updated");
            } else {
                console.log(err);
            }
        });
    });
};
exports.send = function(from, to, msg, callback) {
    User.findOne({username: to}, function(err, user){
        var to_id = user.reg_id;
        var name = to;
        request(
            { method: 'POST',
            uri: 'https://android.googleapis.com/gcm/send',
            headers: {
                'Content-Type': 'application/json',
                'Authorization':'key=AIzaSyCx1rMqRufTCa-c-969mIxrTVRbFbnkkDE'
            },
            body: JSON.stringify({
          "registration_ids" : [to_id],
          "data" : {
            "msg":msg,
            "from":from
          },
          "time_to_live": 108
        })
            }
          , function (error, response, body) {
            callback({'response':"Success"});
            }
          );
    });
}
