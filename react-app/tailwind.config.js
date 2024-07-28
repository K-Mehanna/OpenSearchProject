/** @type {import('tailwindcss').Config} */

export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        background: '#f9f9ff',
        navbar: '#dae2f9', //'#d6e3ff',
        container: '#ebeefa',//'#F9F9F9',
        primary: '#415f91',
        onPrimary: '#FFFFFF',
        hover: '#cad7fc',
        onContainer: '#000000' 
      }
    },
  },
  plugins: [],
}

