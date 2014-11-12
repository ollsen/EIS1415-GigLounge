var assert = require('assert')
var should = require('should');
var mongoose = require('mongoose');
var User = require('../models/user')
var Band = require('../models/band')

describe('Creating Band', function() {
    var url = 'http://localhost:3000/bands';
    var band = {
        bandname: 'Testband',
        country: 'USA',
        city: 'New York',
        genre: 'Rock'
    };
    describe('set Id', function() {
        it('should set the band id to next higher number', function() {
            request(url)
                .post('/new')
                .send(band)
        });
    });
});