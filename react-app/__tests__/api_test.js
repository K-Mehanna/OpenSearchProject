const frisby = require('frisby');

it ('GET should return a status of 200', function () {
  return frisby
    .get('http://localhost:8080/api/v1/search/great')
    .expect('status', 200);
});
