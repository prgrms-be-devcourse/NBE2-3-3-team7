export const getKorDateRange = (startDate, endDate) => {
	const start = new Date(startDate);
	const end = new Date(endDate);
	const sYear = start.getFullYear();
	const sMonth = start.getMonth() + 1;
	const sDay = start.getDate();

	const eYear = end.getFullYear();
	const eMonth = end.getMonth() + 1;
	const eDay = end.getDate();

	return `${sYear}년 ${sMonth < 10 ? '0' + sMonth : sMonth}월 ${sDay < 10 ? '0' + sDay : sDay}일 ~ ${eYear}년 ${eMonth < 10 ? '0' + eMonth : eMonth}월 ${eDay < 10 ? '0' + eDay : eDay}일`;
}

export const getPeriod = (startDate, endDate) => {
	const start = new Date(startDate);
	const end = new Date(endDate);

	return (end - start) / (1000 * 3600 * 24) + 1;
}

export const truncateText = (text, length) => {
	if (text.length <= length) return text;

	return text.slice(0, length) + '...';
}

export const imageSlider = () => {
	const slides = document.getElementById('image-container');
	const slideItems = document.querySelectorAll('#image-container > div');

	let index = 0;

	const totalSlides = slideItems.length;

	const prevButton = document.getElementById('prev-img-btn');
	const nextButton = document.getElementById('next-img-btn');

	const num = document.getElementById('slide-img-num');
	const total = document.getElementById('slide-img-total');
	num.innerHTML = 1;
	total.innerHTML = totalSlides;

	prevButton.addEventListener('click', () => {
		index = (index - 1 + totalSlides) % totalSlides;
		num.innerHTML = index + 1;
		moveSlide(slides, index);
	});

	nextButton.addEventListener('click', () => {
		index = (index + 1) % totalSlides;
		num.innerHTML = index + 1;
		moveSlide(slides, index);
	});

	function moveSlide(slides, index) {
		slides.style.transform = `translateX(-${index * 100}%)`;
	}
}

// function daumPostcode() {
// 	const postcode = document.getElementById('postcode');
// 	const addr = document.getElementById('addr');

// 	new daum.Postcode({
// 		oncomplete: function (data) {
// 			var roadAddr = data.roadAddress; // 도로명 주소 변수

// 			postcode.value = data.zonecode;
// 			addr.value = roadAddr;
// 		}
// 	}).open();
// }