export async function shorten(url: string) {
  const res = await fetch("http://localhost:8080/create", {
    method: "POST",
    body: url,
  });

  if (!res.ok) {
    throw new Error("Error trying to shorten URL");
  }

  const code = await res.text();

  return `http://localhost:8080/${code}`;
}
