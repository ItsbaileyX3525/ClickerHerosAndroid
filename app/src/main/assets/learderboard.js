async function addScore() {
    var score = localStorage.getItem('score')
    var username  = 'Bailey'
    if(!score){return 0}

    const path = 'https://braveriver69.qoom.space/~/homepage/leaderboard'
    const method = 'POST'
    const headers = { 'Content-Type': 'application/json' }
    const body = {username, score}
    const redirect = 'error'

    try{
    await fetch(path, { method, headers, body, redirect })
    } catch(ex) {
        alert("Username in use :(")
    }
}