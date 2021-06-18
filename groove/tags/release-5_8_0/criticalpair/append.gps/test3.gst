<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph role="graph" edgeids="false" edgemode="directed" id="test3">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>123 155 45 36</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>334 161 19 19</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>110 279 19 19</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>308 231 19 19</string>
            </attr>
        </node>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>x</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>control</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>this</string>
            </attr>
        </edge>
        <edge from="n0" to="n2">
            <attr name="label">
                <string>caller</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>this</string>
            </attr>
            <attr name="layout">
                <string>517 -3 145 173 317 240 11</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>x</string>
            </attr>
            <attr name="layout">
                <string>517 -3 145 173 317 240 11</string>
            </attr>
        </edge>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>append</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>val</string>
            </attr>
            <attr name="layout">
                <string>500 0 344 171 393 126 343 170 11</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string></string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>val</string>
            </attr>
            <attr name="layout">
                <string>500 0 318 241 375 244 317 240 11</string>
            </attr>
        </edge>
    </graph>
</gxl>
