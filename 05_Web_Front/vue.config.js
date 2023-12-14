module.exports = {
  lintOnSave: false,
  publicPath: './',
  productionSourceMap: process.env.NODE_ENV !== 'production',
  configureWebpack: {
    output: {
      filename: 'js/[name].[hash:8].js',
    },
  },
  devServer: {
    open: true,
    hot: true,
    https: false,
  },
}
