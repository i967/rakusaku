// UUIDを生成（簡易バージョン）
function generateUUID() {
  return crypto.randomUUID();
}

// Cookieを取得
function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}

// Cookieを設定
function setCookie(name, value, days = 365) {
  const date = new Date();
  date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
  const expires = `expires=${date.toUTCString()}`;
  document.cookie = `${name}=${value}; ${expires}; path=/; SameSite=Lax`;
}

// メイン処理
(function () {
  let userId = getCookie("user_id");

  if (!userId) {
    userId = generateUUID();
    setCookie("user_id", userId);
    console.log("新しいユーザーとして登録しました:", userId);
  } else {
    console.log("既存のユーザーです:", userId);
  }

  document.getElementById("user-info").textContent = `あなたのユーザーID: ${userId}`;
})();