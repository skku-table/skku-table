// next.config.js
const withPWA = require("next-pwa")({
  dest: "public",
  register: true,
  skipWaiting: true,
  disable: process.env.NODE_ENV === 'development',
  // sw: 'firebase-messaging-sw.js',
  // buildExcludes: [/firebase-messaging-sw\.js$/],
});

const nextConfig = {
  reactStrictMode: true,
  output: "standalone",
  compiler:{
    styledComponents: true,
  },
  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "res.cloudinary.com",
      }],
  },
};

module.exports = withPWA(nextConfig);
