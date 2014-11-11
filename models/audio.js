var mongoose = require('mongoose');

module.exports = mongoose.model('Audio', {
    _creator: { type: Number, ref: 'User'},
    date : {type: Date, default: Date.now}
    file: Blob
});