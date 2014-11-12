var mongoose = require('mongoose');

module.exports = mongoose.model('Band', {
    id: { type: Number, index: { unique : true } },
    bandName: String,
    country: String,
    city: String,
    genre: String,
    members: [{ type: mongoose.Schema.Types.ObjectId, ref: 'User'}],
});