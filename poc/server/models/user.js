var mongoose = require('mongoose');

module.exports = mongoose.model('User', {
    username: String,
    password: String,
    email: String,
    firstName: String,
    lastName: String,
    country: String,
    city: String,
    mInstrument: String,
    bands: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Band'}],
    auTracks: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Audio'}]
});