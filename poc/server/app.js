var http = require('http');
var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');
var faye = require('faye');

var db = require('./db');
var User = require('./models/user')
var Band = require('./models/band')
var Audio = require('./models/audio')

/* Faye */
var bayeux = new faye.NodeAdapter({
    mount: '/faye',
    timeout: 45
});

var app = express();
var server = http.createServer(app);

bayeux.attach(server);

bayeux.on('handshake', function(clientId) {
    console.log('client connected ', clientId);
});
// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded());
app.use(methodOverride(function(req, res) {
    if (typeof req.body === 'object') {
        if (req.body._method) {
            // look in urlencoded POST bodies and delete it
            var method = req.body._method;
            delete req.body._method;
            return method;
        }
    }
}));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));


/* Configuring Passport */
var passport = require('passport');
var expressSession = require('express-session');
app.use(expressSession({secret: 'mySecretKey'}));
app.use(passport.initialize());
app.use(passport.session());

var flash = require('connect-flash');
app.use(flash());

var initPassport = require('./passport/init');
initPassport(passport);


var routes = require('./routes/index')(passport);
var users = require('./routes/users')(passport);
var bands = require('./routes/bands')(passport);
var record = require('./routes/record')(passport);
var pubsub = require('./routes/pubsub')(passport);
app.use('/', routes);
app.use('/users', users);
app.use('/bands', bands);
app.use('/record', record);
app.use('/pubsub', pubsub);

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

var port = process.env.PORT || 3000;
server.listen(port);
