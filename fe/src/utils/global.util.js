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

export const formattedKorWon = (amount) => {
    if (isNaN(amount) || amount < 0) return "올바른 금액을 입력하세요";

    const unit = "만원";
    const converted = (amount / 10000).toLocaleString(); // 만 단위로 변환

    return `${converted}${unit}`;
}

export const getStatusClass = (status) => {
	switch (status) {
	  case "임대 완료": // 예: 활성화된 상태
		return "text-[#3FB8AF]";
	  case "결제 완료": // 예: 대기 중 상태
		return "text-gray-700";
	  case "환불 완료": // 예: 취소된 상태
		return "text-red-500";
	  default:
		return "text-gray-700"; // 예: 기본 상태
	}
  };