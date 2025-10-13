const p1buttonplus = document.querySelector("#player1-plus");
const p1buttonminus = document.querySelector("#player1-minus");
const p2buttonplus = document.querySelector("#player2-plus");
const p2buttonminus = document.querySelector("#player2-minus");
const resetbutton = document.querySelector("#reset");

p1buttonplus.addEventListener('click', function () {
    p1display.textContent = parseInt(p1display.textContent) + 1;
});
p1buttonminus.addEventListener('click', function () {
    p1display.textContent = p1display.textContent - 1;
});

p2buttonplus.addEventListener('click', function () {
    p2display.textContent = parseInt(p2display.textContent) + 1;
});
p2buttonminus.addEventListener('click', function () {
    p2display.textContent = p2display.textContent - 1;
});


resetbutton.addEventListener('click', function(){
    p1display.textContent = 0
    p2display.textContent = 0
});