
function search(){
    let word = document.getElementById("word").value;
    const response = fetch(`http://localhost:8080/restaurant/search/${word}/0`, {method:"GET"});
    document.location.href=`http://localhost:8080/restaurant/search/${word}/0`;
    console.log(response);

}


