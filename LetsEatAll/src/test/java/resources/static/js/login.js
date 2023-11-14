const http = new XMLHttpRequest();;
const url = 'http://localhost:8080/user/sign-in';

function tryLogin(){
    let formData = document.getElementById('data');

    let data = {"id":formData.get('id'), "password":formData.get('password')}
    alert(data.toString())
    http.open('POST', url);
    http.send(JSON.parse(data.toString()));

    http.onreadystatechange = (e) =>{
        console.log(http.responseText);
    }
}

