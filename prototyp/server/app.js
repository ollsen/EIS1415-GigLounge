var http = require('http');
var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var busboy = require('connect-busboy');


/*
* Setting up Database with all DB Models for Mongodb
*/
require('./db');
require('./models/user');
require('./models/admin');
require('./models/band');
require('./models/gig');
require('./models/event');
require('./models/search');
require('./models/casting');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));
app.use(busboy());

/* 
* Configuring Passport 
*/
var passport = require('passport');
var expressSession = require('express-session');
app.use(expressSession({secret: 'mySecretKey'}));
app.use(passport.initialize());
app.use(passport.session());
var initPassport = require('./passport/init');
initPassport(passport);

/*
* Setting up routes
*/
app.use('/', require('./routes/index')(passport));
app.use('/users', require('./routes/users')(passport));
app.use('/bands', require('./routes/bands')(passport));
app.use('/events', require('./routes/events')(passport));
app.use('/search', require('./routes/search')(passport));
app.use('/casting', require('./routes/casting')(passport));


// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});

module.exports = app;
