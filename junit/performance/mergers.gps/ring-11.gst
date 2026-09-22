<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="ring-11">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n0">
            <attr name="layout">
                <string>40 40 30 30</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>130 40 30 30</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>220 40 30 30</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>310 40 30 30</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>40 110 30 30</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>130 110 30 30</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>220 110 30 30</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>310 110 30 30</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>40 180 30 30</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>130 180 30 30</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>220 180 30 30</string>
            </attr>
        </node>
        <edge from="n0" to="n0">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge from="n1" to="n1">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge from="n2" to="n2">
            <attr name="label">
                <string>flag:c</string>
            </attr>
        </edge>
        <edge from="n3" to="n3">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge from="n4" to="n4">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge from="n5" to="n5">
            <attr name="label">
                <string>flag:c</string>
            </attr>
        </edge>
        <edge from="n6" to="n6">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge from="n7" to="n7">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge from="n8" to="n8">
            <attr name="label">
                <string>flag:c</string>
            </attr>
        </edge>
        <edge from="n9" to="n9">
            <attr name="label">
                <string>flag:a</string>
            </attr>
        </edge>
        <edge from="n10" to="n10">
            <attr name="label">
                <string>flag:b</string>
            </attr>
        </edge>
        <edge from="n0" to="n1">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge from="n0" to="n3">
            <attr name="label">
                <string>a_to_a</string>
            </attr>
        </edge>
        <edge from="n1" to="n2">
            <attr name="label">
                <string>b_to_c</string>
            </attr>
        </edge>
        <edge from="n2" to="n3">
            <attr name="label">
                <string>c_to_a</string>
            </attr>
        </edge>
        <edge from="n2" to="n5">
            <attr name="label">
                <string>c_to_c</string>
            </attr>
        </edge>
        <edge from="n3" to="n4">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge from="n4" to="n5">
            <attr name="label">
                <string>b_to_c</string>
            </attr>
        </edge>
        <edge from="n4" to="n7">
            <attr name="label">
                <string>b_to_b</string>
            </attr>
        </edge>
        <edge from="n5" to="n6">
            <attr name="label">
                <string>c_to_a</string>
            </attr>
        </edge>
        <edge from="n6" to="n7">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge from="n6" to="n9">
            <attr name="label">
                <string>a_to_a</string>
            </attr>
        </edge>
        <edge from="n7" to="n8">
            <attr name="label">
                <string>b_to_c</string>
            </attr>
        </edge>
        <edge from="n8" to="n9">
            <attr name="label">
                <string>c_to_a</string>
            </attr>
        </edge>
        <edge from="n8" to="n0">
            <attr name="label">
                <string>c_to_a</string>
            </attr>
        </edge>
        <edge from="n9" to="n10">
            <attr name="label">
                <string>a_to_b</string>
            </attr>
        </edge>
        <edge from="n10" to="n0">
            <attr name="label">
                <string>b_to_a</string>
            </attr>
        </edge>
        <edge from="n10" to="n2">
            <attr name="label">
                <string>b_to_c</string>
            </attr>
        </edge>
    </graph>
</gxl>
