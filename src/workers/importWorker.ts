self.onmessage = () => {
  self.postMessage({
    status: "error",
    message: "Import worker integration is alpha; main-thread fallback is used for unsupported jobs."
  });
};
