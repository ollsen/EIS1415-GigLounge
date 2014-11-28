var mongoose = require('mongoose');

var MyModel = new mongoose.Schema({
    username: String,
    password: String,
    email: String,
    firstName: String,
    lastName: String,
    country: String,
    city: String,
    mInstrument: String,
    reg_id: String,
    bands: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Band'}],
    auTracks: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Audio'}]
});

module.exports = mongoose.model('User', MyModel);