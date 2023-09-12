function SmoothTransit(toPage) {
    const overlay = document.createElement('div');
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100vw';
    overlay.style.height = '100vh';
    overlay.style.backgroundColor = 'black';
    overlay.style.opacity = '0';
    document.body.appendChild(overlay);
    const allMediaElements = document.querySelectorAll('audio, video');


    let opacity = 0;

    const fadeInInterval = setInterval(() => {
        opacity += 0.1;
        allMediaElements.forEach((element) => {
            element.volume-=0.0001;
        });
        overlay.style.opacity = opacity.toString();

        if (opacity >= 1) {
            clearInterval(fadeInInterval);
            window.location.href = toPage+".html"
        }
    }, 15);
}

function UnSmoothTransit() {
    const egg = document.getElementById('Smooth')

    let opacity = 1;
    

    const fadeInInterval = setInterval(() => {
        opacity -= 0.1;
        egg.style.opacity = opacity.toString();

        if (opacity <= -0) {
            document.body.removeChild(egg)
            clearInterval(fadeInInterval);
        }
    }, 15);
}
UnSmoothTransit();