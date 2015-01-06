var LocalStrategy = require('passport-local').Strategy;
var db = require('../db');
var User = db.model('User');
var bCrypt = require('bcrypt-nodejs');

module.exports = function(passport) {
    passport.use('signup', new LocalStrategy({
        passReqToCallback : true
    },
    function(req, username, password, done) {
        findOrCreateUser = function() {
            // find a user in Mongo with provided username
            User.findOne({ 'email' : username }, function(err, user){
                // in case of any error return
                if (err) {
                    console.log('Error in Signup '+err);
                    return done(err);
                }
                // already exists
                if (user) {
                    //console.log('User already exists');
                    return done(null, false, {'message' : 'User Already exists'});
                } else {
                    // if there is no user with that email
                    // create the user
                    var newUser = new User();
                    // set the user's local credentials
                    newUser.email = username;
                    newUser.password = createHash(password);
                    newUser.firstName = req.param('firstName');
                    newUser.lastName = req.param('lastName');
                    
                    // save new user
                    newUser.save(function(err) {
                        if (err) {
                            console.log('Error in Saving user: '+err);
                            throw err;
                        }
                        //console.log('User Registration succesful');
                        return done(null, newUser, {'message' : 'User Registration succesful'});
                    });
                }
            });
        };
        
        // Delay the execution of FindOrCreateUser and execute
        // the method in the next tick of the event loop
        process.nextTick(findOrCreateUser);
    })
);
    // Generates hash using bCrypt
    var createHash = function(password) {
        return bCrypt.hashSync(password, bCrypt.genSaltSync(10), null);
    }
}